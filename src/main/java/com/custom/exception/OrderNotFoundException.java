package com.custom.exception;

public class OrderNotFoundException extends RuntimeException{

	public OrderNotFoundException(String message) {
        super(message);
    }
}
