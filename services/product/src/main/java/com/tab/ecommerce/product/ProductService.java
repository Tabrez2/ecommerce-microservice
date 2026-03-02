package com.tab.ecommerce.product;

import com.tab.ecommerce.exception.ProductNotFoundException;
import com.tab.ecommerce.exception.ProductPurchaseException;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;

@Service
@RequiredArgsConstructor
public class ProductService {
    private final ProductRepository repository;
    private final ProductMapper mapper;
    public Integer saveProduct(Product product) {
         return repository.save(product).getId();
    }

    public ProductResponse findById(Integer productId) {
        var product = repository.findById(productId)
                .orElseThrow(() -> new ProductNotFoundException("Product not found"));
        return mapper.toProductResponse(product);
    }

    public List<ProductResponse> findAll() {
        var productList=repository.findAll();

        return productList.stream()
                .map(mapper::toProductResponse)
                .toList();

    }

    public List<ProductPurchaseResponse> purchaseProducts(List<ProductPurchaseRequest> request) {
        var productId =request.stream()
                .map(ProductPurchaseRequest::productId)
                .toList();
        var requestedProducts = repository.findAllByIdInOrderById(productId);

        if(productId.size() != requestedProducts.size()) {
            throw new ProductPurchaseException("one or more products are not available ");
        }

        var sortedRequest =request.stream()
                .sorted(Comparator.comparing(ProductPurchaseRequest::productId))
                .toList();

        var purchasedProducts = new ArrayList<ProductPurchaseResponse>();

        for(int i = 0; i < requestedProducts.size(); i++) {
         var product = requestedProducts.get(i);
         var productRequest = sortedRequest.get(i);
           if(productRequest.quantity()>product.getAvailableQuantity())
                throw new ProductPurchaseException("product quantity exceeds available quantity");

            var newAvailableQuantity = product.getAvailableQuantity() - productRequest.quantity();
                product.setAvailableQuantity(newAvailableQuantity);
                repository.save(product);
            purchasedProducts.add(mapper.toproductPurchaseResponse(product,productRequest.quantity()));
        }

        return purchasedProducts;
    }

}
