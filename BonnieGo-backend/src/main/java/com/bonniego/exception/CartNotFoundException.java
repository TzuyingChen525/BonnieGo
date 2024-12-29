package com.bonniego.exception;

public class CartNotFoundException extends RuntimeException {

    public CartNotFoundException() {
        super("查無購物車");
    }
    public CartNotFoundException(String message) {
        super(message);
    }




}
