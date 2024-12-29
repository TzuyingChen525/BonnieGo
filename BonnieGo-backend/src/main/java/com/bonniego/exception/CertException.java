package com.bonniego.exception;

public class CertException extends Exception {
    public CertException(String message) {
        super(message);
    }
    public CertException() {
        super("登入憑證發生錯誤");
    }
}
