package com.web.ecommerce.dto.user;

import com.web.ecommerce.model.user.Address;
import lombok.Builder;
import lombok.Data;

import java.util.List;
import java.util.stream.Collectors;

@Data
@Builder
public class AddressDTO {
    private Long id;
    private String firstname;
    private String lastname;
    private String phoneNumber;
    private String street;
    private String houseNumber;
    private String city;
    private String country;
    private String zipCode;
    private Boolean isDefault;

    public static AddressDTO toAddressDTO(Address address){
        return AddressDTO.builder()
                .id(address.getId())
                .firstname(address.getFirstname())
                .lastname(address.getLastname())
                .phoneNumber(address.getPhoneNumber())
                .street(address.getStreet())
                .houseNumber(address.getHouseNumber())
                .city(address.getCity())
                .country(address.getCountry())
                .zipCode(address.getZipCode())
                .isDefault(address.isDefault())
                .build();
    }

    public static List<AddressDTO> addressDTOS(List<Address> addresses){
        return addresses.stream().map(AddressDTO::toAddressDTO).collect(Collectors.toList());
    }

}
