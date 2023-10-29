package com.web.ecommerce.model;

import com.web.ecommerce.dto.user.SignUpData;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class SignUpRequest {
    private SignUpData signUpData;
    private String deviceId;
}
