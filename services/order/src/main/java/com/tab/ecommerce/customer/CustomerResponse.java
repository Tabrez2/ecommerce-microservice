package com.tab.ecommerce.customer;

import com.fasterxml.jackson.annotation.JsonProperty;

public record CustomerResponse(
        String id,
        @JsonProperty("firstName") // Try "firstName" or "first_name"
        String firstName,
        @JsonProperty("lastName")  // Try "lastName" or "last_name"
        String lastName,
        String email
) {

}