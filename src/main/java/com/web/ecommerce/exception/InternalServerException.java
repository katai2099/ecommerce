package com.web.ecommerce.exception;

public class InternalServerException extends RuntimeException{
    public InternalServerException(){
        super("Internal Server Error");
    }
}
