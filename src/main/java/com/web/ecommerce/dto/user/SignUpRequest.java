package com.web.ecommerce.dto.user;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class SignUpRequest {
    private String firstname;
    private String lastname;
    private String email;
    private String password;
}
