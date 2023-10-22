package com.web.ecommerce.model;

import lombok.Getter;
import lombok.Setter;

public class ProductAttributeRequest {
    @Getter
    @Setter
    public static class ProductFeaturedRequest{
        private Boolean featured;
    }
    @Getter
    @Setter
    public static class ProductPublishRequest{
        private Boolean publish;
    }

    @Getter
    @Setter
    public static class CategoryIsTopRequest{
        private Boolean isTop;
    }

}
