package com.tab.ecommerce.kafka;

import com.tab.ecommerce.customer.CustomerResponse;
import com.tab.ecommerce.order.PaymentMethod;
import com.tab.ecommerce.product.PurchaseResponse;

import java.math.BigDecimal;
import java.util.List;

public record OrderConfirmation (
        String orderReference,
        BigDecimal totalAmount,
        PaymentMethod paymentMethod,
        CustomerResponse customer,
        List<PurchaseResponse> products

) {
}