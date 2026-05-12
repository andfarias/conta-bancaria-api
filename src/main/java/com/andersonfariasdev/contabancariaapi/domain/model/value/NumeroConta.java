package com.andersonfariasdev.contabancariaapi.domain.model.value;

import com.andersonfariasdev.contabancariaapi.infrastructure.exception.ValidationException;

import java.util.Objects;

public final class NumeroConta {

    private final String valor;

    public NumeroConta(String valor) {
        if (valor == null || valor.trim().isEmpty()) {
            throw new ValidationException("numero da conta é obrigatório");
        }
        this.valor = valor.trim();
    }

    public String getValor() {
        return valor;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        NumeroConta that = (NumeroConta) o;
        return Objects.equals(valor, that.valor);
    }

    @Override
    public int hashCode() {
        return Objects.hash(valor);
    }

    @Override
    public String toString() {
        return valor;
    }
}
