package com.tab.ecommerce.product;


import java.math.BigDecimal;

public record ProductRequest(
         Integer id,
         String name,
         String description,
         double availableQuantity,
         BigDecimal price,
         Integer categoryId
) {
}
