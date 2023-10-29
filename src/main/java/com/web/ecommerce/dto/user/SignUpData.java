package com.web.ecommerce.dto.user;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class SignUpData {
    private String firstname;
    private String lastname;
    private String email;
    private String password;
}
