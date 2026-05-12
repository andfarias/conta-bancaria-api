package com.andersonfariasdev.contabancariaapi.infrastructure.exception;

public class ValidationException extends RuntimeException {
    public ValidationException(String message) { super(message); }
}
