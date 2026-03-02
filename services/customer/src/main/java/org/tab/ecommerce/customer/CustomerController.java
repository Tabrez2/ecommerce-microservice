package org.tab.ecommerce.customer;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/v1/customer")
@RequiredArgsConstructor
public class CustomerController {

    private final CustomerService service;

    @PostMapping
    public ResponseEntity<String> createCustomer(
            @RequestBody @Valid CustomerRequest request
    ){
        return ResponseEntity.ok(service.createCustomer(request));
    }

    @PutMapping
    public ResponseEntity<?> updateCustomer(@RequestBody CustomerRequest request){
        service.updateCustomer(request);
        return ResponseEntity.accepted().build();
    }
    @GetMapping
    public ResponseEntity<?> getAllCustomers() {
        try {
            return ResponseEntity.ok(service.findAllCustomer());
        } catch (Exception e) {
            e.printStackTrace(); // Log to console
            return ResponseEntity.status(500).body("Error: " + e.toString());
        }
    }
    @GetMapping("/exists/{customer-id}")
    public ResponseEntity<Boolean> existsById
            (@PathVariable("customer-id") String customerId) {
            return ResponseEntity.ok(service.existsById(customerId));
    }

    @GetMapping("/{customer-id}")
    public ResponseEntity<CustomerResponse> findById
            (@PathVariable("customer-id") String customerId) {
            return ResponseEntity.ok(service.findById(customerId));

    }
    @DeleteMapping("/{customer-id}")
    public ResponseEntity<Void> deleteCustomerById
            (@PathVariable("customer-id") String customerId) {
                this.service.deleteById(customerId);
               return ResponseEntity.accepted().build();
    }

}
