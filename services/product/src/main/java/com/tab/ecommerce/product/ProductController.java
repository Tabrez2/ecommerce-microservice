package com.tab.ecommerce.product;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/v1/product")
@RequiredArgsConstructor
public class ProductController {

    private final ProductService service;
    private final ProductMapper mapper;

    @PostMapping
    public ResponseEntity<Integer> createProduct(@RequestBody @Valid ProductRequest request) {
        var product = mapper.toProduct(request);
        return ResponseEntity.ok(service.saveProduct(product));
    }

    @PostMapping("/purchase")
    public  ResponseEntity<List<ProductPurchaseResponse>>purchaseProduct(@RequestBody List<ProductPurchaseRequest> request){
        return ResponseEntity.ok(service.purchaseProducts(request));
    }

    @GetMapping("/{product-id}")
    public ResponseEntity<ProductResponse> findProductById(@PathVariable("product-id") Integer productId) {
        return ResponseEntity.ok(service.findById(productId));
    }
    @GetMapping("/findAll")
    public ResponseEntity<List<ProductResponse>> findAllProducts() {
        return ResponseEntity.ok(service.findAll());
    }

}
