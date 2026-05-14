package com.andersonfariasdev.contabancariaapi.domain.model.value;


import com.andersonfariasdev.contabancariaapi.infrastructure.exception.ValidationException;

public record IdentificadorConta(String agencia, String conta, String digito) {
    public IdentificadorConta {
        if (agencia == null || agencia.trim().isEmpty()) {
            agencia = "0001";
        }
        if (conta == null || !conta.matches("\\d{6}")) {
            throw new ValidationException("Número da conta deve 6 dígitos numéricos.");
        }
        if (digito == null || !digito.matches("\\d{1}|X|x")) {
            throw new ValidationException("Dígito verificador inválido.");
        }
        // TODO: implementar o Algoritmo de Módulo 10 ou 11 para validar o dígito
    }

    public IdentificadorConta(String conta, String digito) {
        this("0001", conta, digito);
    }

    public String getFullNumber() {
        return agencia + "-" + conta + "-" + digito;
    }
}

