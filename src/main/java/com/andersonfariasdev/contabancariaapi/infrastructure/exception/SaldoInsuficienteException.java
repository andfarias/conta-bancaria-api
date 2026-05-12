package com.andersonfariasdev.contabancariaapi.infrastructure.exception;

public class SaldoInsuficienteException extends RuntimeException {
    public SaldoInsuficienteException(String message) {
        super(message);
    }
}
