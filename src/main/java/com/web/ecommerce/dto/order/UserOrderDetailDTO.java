package com.web.ecommerce.dto.order;

import com.web.ecommerce.model.user.User;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class UserOrderDetailDTO {
    private String email;
    private String firstname;
    private String lastname;

    public static UserOrderDetailDTO toUserOrderDetailDTO(User user){
        UserOrderDetailDTO dto = new UserOrderDetailDTO();
        dto.setEmail(user.getEmail());
        dto.setFirstname(user.getFirstname());
        dto.setLastname(user.getLastname());
        return dto;
    }
}
