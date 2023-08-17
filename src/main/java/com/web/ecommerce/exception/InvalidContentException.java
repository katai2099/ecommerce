package com.web.ecommerce.exception;

public class InvalidContentException extends RuntimeException{
    public InvalidContentException(String message){
        super(message);
    }
}
