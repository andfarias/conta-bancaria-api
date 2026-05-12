package com.andersonfariasdev.contabancariaapi.domain.model;

import com.andersonfariasdev.contabancariaapi.domain.model.value.NumeroConta;
import com.andersonfariasdev.contabancariaapi.infrastructure.exception.ValidationException;

import java.math.BigDecimal;
import java.util.Objects;

public class ContaBancaria {

    private final Long id;
    private final NumeroConta numero;
    private final String digitoVerificador;
    private final String documento;
    private BigDecimal saldo;

    public ContaBancaria(Long id, NumeroConta numero, String digitoVerificador, String documento, BigDecimal saldo) {
        if (numero == null) throw new ValidationException("numero é obrigatório");
        if (digitoVerificador == null || digitoVerificador.trim().isEmpty()) throw new ValidationException("digito verificador é obrigatório");
        if (documento == null || documento.trim().isEmpty()) throw new ValidationException("documento é obrigatório");
        this.id = id;
        this.numero = numero;
        this.digitoVerificador = digitoVerificador.trim();
        this.documento = documento.trim();
        this.saldo = saldo == null ? BigDecimal.ZERO : saldo;
    }

    public Long getId() {
        return id;
    }

    public NumeroConta getNumero() {
        return numero;
    }

    public String getDigitoVerificador() {
        return digitoVerificador;
    }

    public String getDocumento() {
        return documento;
    }

    public BigDecimal getSaldo() {
        return saldo;
    }

    public void creditar(BigDecimal valor) {
        if (valor == null || valor.signum() <= 0) throw new ValidationException("valor deve ser positivo");
        this.saldo = this.saldo.add(valor);
    }

    public void debitar(BigDecimal valor) {
        if (valor == null || valor.signum() <= 0) throw new ValidationException("valor deve ser positivo");
        if (this.saldo.compareTo(valor) < 0) throw new ValidationException("saldo insuficiente");
        this.saldo = this.saldo.subtract(valor);
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        ContaBancaria that = (ContaBancaria) o;
        return Objects.equals(id, that.id) && Objects.equals(numero, that.numero);
    }

    @Override
    public int hashCode() {
        return Objects.hash(id, numero);
    }
}
