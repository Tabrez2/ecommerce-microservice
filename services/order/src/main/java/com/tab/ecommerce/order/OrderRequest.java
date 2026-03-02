package com.tab.ecommerce.order;

import com.tab.ecommerce.product.PurchaseRequest;
import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;

import java.math.BigDecimal;
import java.util.List;

public record OrderRequest(
        Integer id,
        String reference,
        @Positive(message = "Order amount should be positive")
        BigDecimal amount,
        @NotNull(message = "payment should be precised")
        PaymentMethod paymentMethod,
        @NotNull(message = "customer should be present")
        String customerId,
        @NotEmpty(message = "you should purchase atleast one product")
        List<PurchaseRequest> products
) {
}
