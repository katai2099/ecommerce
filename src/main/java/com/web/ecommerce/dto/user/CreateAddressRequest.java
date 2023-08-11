package com.web.ecommerce.dto.user;

import lombok.Data;

@Data
public class CreateAddressRequest {
    private Long userId;
    private String street;
    private String city;
    private String state;
    private String country;
    private String zip;
}
