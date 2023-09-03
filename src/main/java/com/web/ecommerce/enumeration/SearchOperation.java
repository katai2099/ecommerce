package com.web.ecommerce.enumeration;

public enum SearchOperation {
    EQUAL,GREATER_THAN,GREATER_THAN_EQUAL,LESS_THAN,LESS_THAN_EQUAL;

    public static SearchOperation getSimpleOperation(final String input){
        return switch (input) {
            case "eq" -> EQUAL;
            case "gt" -> GREATER_THAN;
            case "ge" -> GREATER_THAN_EQUAL;
            case "lt" -> LESS_THAN;
            case "le" -> LESS_THAN_EQUAL;
            default -> null;
        };
    }

    @Override
    public String toString() {
        return switch (this){
            case EQUAL -> "eq";
            case GREATER_THAN -> "gt";
            case GREATER_THAN_EQUAL -> "ge";
            case LESS_THAN -> "lt";
            case LESS_THAN_EQUAL -> "le";
        };
    }
}
