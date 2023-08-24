package com.web.ecommerce.model.user;

public enum Role {
    ROLE_ADMIN,
    ROLE_USER;

    @Override
    public String toString() {
        switch (this){
            case ROLE_USER -> {
                return "USER";
            }
            case ROLE_ADMIN -> {
                return "ADMIN";
            }
            default -> {
                return "NO MATCH";
            }
        }
    }
}
