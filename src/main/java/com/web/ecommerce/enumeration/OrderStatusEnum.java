package com.web.ecommerce.enumeration;

public enum OrderStatusEnum {
    PURCHASED, PROCESSING, SHIPPED, DELIVERED, CANCELLED;

    public static OrderStatusEnum getNextOrderStatus(OrderStatusEnum currentStatus) {
        int nextOrderStatusIndex = (currentStatus.ordinal() + 1) % OrderStatusEnum.values().length;
        return OrderStatusEnum.values()[nextOrderStatusIndex];
    }

    @Override
    public String toString() {
        return switch (this) {
            case PURCHASED -> "PURCHASED";
            case PROCESSING -> "PROCESSING";
            case SHIPPED -> "SHIPPED";
            case DELIVERED -> "DELIVERED";
            case CANCELLED -> "CANCELLED";
        };
    }
}
