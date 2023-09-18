package com.web.ecommerce.dto.user;

import com.web.ecommerce.model.user.User;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class UserOrderDetailDTO {
    private String email;
    private String street;
    private String city;
    private String state;
    private String country;
    private String zip;

    public static UserOrderDetailDTO toUserOrderDetailDTO(User user){
        UserOrderDetailDTO dto = new UserOrderDetailDTO();
        dto.setEmail(user.getEmail());
        dto.setStreet(user.getAddress().stream().toList().get(1).getStreet());
        dto.setCity(user.getAddress().stream().toList().get(1).getCity());
//        dto.setState(user.getAddresses().stream().toList().get(1).getState());
        dto.setCountry(user.getAddress().stream().toList().get(1).getCountry());
        dto.setZip(user.getAddress().stream().toList().get(1).getZipCode());
        return dto;
    }

}


