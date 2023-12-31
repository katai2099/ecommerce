package com.web.ecommerce.dto.user;

import lombok.Builder;
import lombok.Data;

@Builder
@Data
public class UserDTO {
    private String firstname;
    private String lastname;
    private String email;
    private String role;
    private String token;
}
