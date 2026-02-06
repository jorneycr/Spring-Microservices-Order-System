package com.ordersystem.user.domain.model;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.EqualsAndHashCode;
import lombok.Getter;

/**
 * Address Value Object
 * Inmutable
 */
@Getter
@Builder
@AllArgsConstructor
@EqualsAndHashCode
public class Address {
    private final String street;
    private final String city;
    private final String state;
    private final String zipCode;
    private final String country;

    public String getFullAddress() {
        return String.format("%s, %s, %s %s, %s", 
            street, city, state, zipCode, country);
    }
}
