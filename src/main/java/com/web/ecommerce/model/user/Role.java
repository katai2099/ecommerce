package com.web.ecommerce.model.user;

public enum Role {
    USER,
    ADMIN;

    @Override
    public String toString() {
        switch (this){
            case USER -> {
                return "USER";
            }
            case ADMIN -> {
                return "ADMIN";
            }
            default -> {
                return "NO MATCH";
            }
        }
    }
}
