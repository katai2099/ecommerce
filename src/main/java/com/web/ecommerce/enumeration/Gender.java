package com.web.ecommerce.enumeration;

public enum Gender {
    MEN, WOMEN;
    @Override
    public String toString() {
        return switch (this) {
            case MEN -> "MEN";
            case WOMEN -> "WOMEN";
        };
    }
}
