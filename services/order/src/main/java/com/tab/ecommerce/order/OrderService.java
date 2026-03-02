package com.tab.ecommerce.order;

import com.tab.ecommerce.kafka.OrderConfirmation;
import com.tab.ecommerce.customer.CustomerClient;
import com.tab.ecommerce.exception.BusinessException;
import com.tab.ecommerce.kafka.OrderProducer;
import com.tab.ecommerce.orderline.OrderLine;
import com.tab.ecommerce.orderline.OrderLineRequest;
import com.tab.ecommerce.orderline.OrderLineService;
import com.tab.ecommerce.payment.PaymentClient;
import com.tab.ecommerce.payment.PaymentRequest;
import com.tab.ecommerce.product.ProductClient;
import com.tab.ecommerce.product.PurchaseRequest;
import jakarta.persistence.EntityNotFoundException;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class OrderService {

    private final OrderRepository repository;
    private final OrderMapper mapper;
    private final CustomerClient customerClient;
    private final PaymentClient paymentClient;
    private final ProductClient productClient;
    private final OrderLineService orderLineService;
    private final OrderProducer orderProducer;

    @Transactional
    public Integer createOrder(OrderRequest request) {

        var customer = customerClient.findCustomerById(request.customerId())
                .orElseThrow(() ->
                        new BusinessException("No customer exists with the provided ID"));

        System.out.println("DEBUG: Customer retrieved: " + customer);

        var purchasedProducts = productClient.purchaseProducts(request.products());

        Order order = mapper.toOrder(request);

        for (PurchaseRequest purchase : request.products()) {
            OrderLine line = OrderLine.builder()
                    .productId(purchase.productId())
                    .quantity(purchase.quantity())
                    .order(order)              // 🔑 owning side
                    .build();

            order.getOrderLines().add(line); // 🔑 sync both sides
        }

        repository.save(order);              // 🔥 ONE save only

        paymentClient.requestOrderPayment(
                new PaymentRequest(
                        request.amount(),
                        request.paymentMethod(),
                        order.getId(),
                        order.getReference(),
                        customer
                )
        );

        orderProducer.sendOrderConfirmation(
                new OrderConfirmation(
                        request.reference(),
                        request.amount(),
                        request.paymentMethod(),
                        customer,
                        purchasedProducts
                )
        );

        return order.getId();
    }
    public List<OrderResponse> findAllOrders() {
        return this.repository.findAll()
                .stream()
                .map(this.mapper::fromOrder)
                .collect(Collectors.toList());
    }

    public OrderResponse findById(Integer id) {
        return this.repository.findById(id)
                .map(this.mapper::fromOrder)
                .orElseThrow(() -> new EntityNotFoundException(String.format("No order found with the provided ID: %d", id)));
    }
}