package com.web.ecommerce.enumeration;

public enum OrderStatusEnum {
    ORDER_PLACED, PROCESSING, OUT_FOR_DELIVERY, DELIVERED;
    @Override
    public String toString() {
        return switch (this) {
            case ORDER_PLACED -> "ORDER PLACED";
            case PROCESSING -> "PROCESSING";
            case OUT_FOR_DELIVERY -> "OUT FOR DELIVERY";
            case DELIVERED -> "DELIVERED";
        };
    }
}
