package com.tab.ecommerce.payment;

import com.tab.ecommerce.order.PaymentMethod;
import com.tab.ecommerce.customer.CustomerResponse;

import java.math.BigDecimal;

public record PaymentRequest(
        BigDecimal amount,
        PaymentMethod paymentMethod,
        Integer orderId,
        String orderReference,
        CustomerResponse customer
) {
}