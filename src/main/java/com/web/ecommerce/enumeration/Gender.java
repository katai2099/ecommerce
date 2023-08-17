package com.web.ecommerce.enumeration;

public enum Gender {
    MEN, WOMEN;
    @Override
    public String toString() {
        switch (this){
            case MEN -> {
                return "MEN";
            }
            case WOMEN -> {
                return "WOMEN";
            }
            default -> {
                return "No Match";
            }
        }
    }
}
