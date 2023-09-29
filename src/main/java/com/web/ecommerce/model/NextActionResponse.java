package com.web.ecommerce.model;

import lombok.Builder;
import lombok.Getter;
import lombok.Setter;

@Setter
@Getter
@Builder
public class NextActionResponse {
    String status;
    String clientSecret;
}
