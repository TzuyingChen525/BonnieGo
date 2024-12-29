package com.bonniego.exception;

public class ProductNotFoundException extends RuntimeException {

    public ProductNotFoundException() {
        super("查無商品");
    }
    public ProductNotFoundException(String message) {
        super(message);
    }




}
