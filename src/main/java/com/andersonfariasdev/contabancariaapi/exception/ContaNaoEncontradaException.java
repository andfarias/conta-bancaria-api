package com.andersonfariasdev.contabancariaapi.exception;

public class ContaNaoEncontradaException extends RuntimeException {
    public ContaNaoEncontradaException(String conta) {
        super("Conta não encontrada: " + conta);
    }
}
