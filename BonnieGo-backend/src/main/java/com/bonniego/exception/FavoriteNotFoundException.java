package com.bonniego.exception;

public class FavoriteNotFoundException extends RuntimeException {

    public FavoriteNotFoundException() {
        super("查無收藏區");
    }
    public FavoriteNotFoundException(String message) {
        super(message);
    }




}
