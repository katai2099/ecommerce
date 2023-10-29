package com.web.ecommerce.model;

import com.web.ecommerce.dto.user.SignInData;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class SignInRequest {
    SignInData signInData;
    String deviceId;
}
