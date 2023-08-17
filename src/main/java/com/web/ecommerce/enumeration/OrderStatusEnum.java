package com.web.ecommerce.enumeration;

public enum OrderStatusEnum {
    PURCHASED, PROCESSING, SHIPPED, DELIVERED, CANCELLED;

    public static OrderStatusEnum getNextOrderStatus(OrderStatusEnum currentStatus) {
        int nextOrderStatusIndex = (currentStatus.ordinal() + 1) % OrderStatusEnum.values().length;
        return OrderStatusEnum.values()[nextOrderStatusIndex];
    }
    @Override
    public String toString() {
        switch (this) {
            case PURCHASED -> {
                return "PURCHASED";
            }
            case PROCESSING -> {
                return "PROCESSING";
            }
            case SHIPPED -> {
                return "SHIPPED";
            }
            case DELIVERED -> {
                return "DELIVERED";
            }
            case CANCELLED -> {
                return "CANCELLED";
            }
            default -> {
                return "No match";
            }
        }
    }
}
