package org.tab.ecommerce.customer;
import lombok.RequiredArgsConstructor;
import org.springframework.util.ObjectUtils;
import org.apache.commons.lang.StringUtils;
import org.springframework.stereotype.Service;
import org.tab.ecommerce.exception.CustomerNotFoundException;

import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class CustomerService {

    private final CustomerRepository repository;
    private final CustomerMapper mapper;

    public String createCustomer(CustomerRequest request) {
        var customer = mapper.toCustomer(request);
        repository.save(customer);
        return customer.getId();
    }

    public void updateCustomer(CustomerRequest request) {
        var customer = repository.findById(request.id())
                .orElseThrow(()-> new CustomerNotFoundException(
                        String.format("customer not found with id:: %s",request.id())
                ));
        mergeCustomer(customer,request);
        repository.save(customer);
    }

    private void mergeCustomer(Customer customer, CustomerRequest request) {
        if(StringUtils.isNotBlank(request.id())){
            customer.setId(request.id());
        } else if (StringUtils.isNotBlank(request.firstName())) {
            customer.setFirstName(request.firstName());
        }
        else if (StringUtils.isNotBlank(request.lastName())) {
            customer.setLastName(request.lastName());
        }
        else if (StringUtils.isNotBlank(request.email())) {
            customer.setEmail(request.email());
        }
        else if (!ObjectUtils.isEmpty(request.address())) {
            customer.setAddress(request.address());
        }
    }

    public  List<CustomerResponse> findAllCustomer() {
        return repository.findAll()
                .stream()
                .map(mapper::fromCustomer)
                .collect(Collectors.toList());
    }

    public  Boolean existsById(String customerId) {
          return repository
                .findById(customerId)
                .isPresent();
    }


    public CustomerResponse findById(String customerId) {
    var cust= repository.findById(customerId);

    return repository.findById(customerId)
                .map(mapper::fromCustomer)
              .orElseThrow(()-> new CustomerNotFoundException(
                      String.format("customer not found with id:: %s",customerId)
              ));
    }

    public void deleteById(String customerId) {
        repository.deleteById(customerId);
    }
}
