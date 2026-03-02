package com.tab.ecommerce.product;

import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;
import org.springframework.validation.annotation.Validated;

@Validated
public record PurchaseRequest(

  @NotNull(message = "product is mandatory")
  Integer productId,
  @Positive(message = "quantity is mandatory")
  double quantity


) {
}
