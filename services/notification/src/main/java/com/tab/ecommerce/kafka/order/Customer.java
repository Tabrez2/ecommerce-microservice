package com.tab.ecommerce.kafka.order;

import com.fasterxml.jackson.annotation.JsonProperty;

public record Customer(
        String id,
        @JsonProperty("firstName")
        String firstName,
        @JsonProperty("lastName")
        String lastName,
        @JsonProperty("email")
        String email
) {

}
