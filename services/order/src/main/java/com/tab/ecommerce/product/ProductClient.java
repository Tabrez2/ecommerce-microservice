package com.tab.ecommerce.product;

import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;

import java.util.List;

@FeignClient(name = "PRODUCT-SERVICE")
public interface ProductClient {

    @PostMapping("/api/v1/product/purchase")
    List<PurchaseResponse> purchaseProducts(
            @RequestBody List<PurchaseRequest> request
    );
}