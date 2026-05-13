package com.andersonfariasdev.contabancariaapi.domain.model;

import com.andersonfariasdev.contabancariaapi.domain.model.enums.StatusConta;
import com.andersonfariasdev.contabancariaapi.domain.model.enums.TipoConta;
import com.andersonfariasdev.contabancariaapi.domain.model.value.NumeroConta;
import com.andersonfariasdev.contabancariaapi.infrastructure.exception.ValidationException;

import java.math.BigDecimal;
import java.util.Objects;

public class ContaBancaria {

    private final Long id;
    private final NumeroConta numero;
    private final String digitoVerificador;
    private final Cooperado titular;
    private final TipoConta tipo;
    private StatusConta status;
    private BigDecimal saldo;

    public ContaBancaria(Long id, NumeroConta numero, String digitoVerificador, Cooperado titular, TipoConta tipo, StatusConta status, BigDecimal saldo) {
        validaCriacaoConta(numero, digitoVerificador, titular, tipo);
        if (status == null) status = StatusConta.ATIVA;
        this.id = id;
        this.numero = numero;
        this.digitoVerificador = digitoVerificador.trim();
        this.titular = titular;
        this.tipo = tipo;
        this.status = status;
        this.saldo = saldo == null ? BigDecimal.ZERO : saldo;
    }

    public ContaBancaria(Long id, NumeroConta numero, String digitoVerificador, Cooperado titular, TipoConta tipo) {
        validaCriacaoConta(numero, digitoVerificador, titular, tipo);
        this.id = id;
        this.numero = numero;
        this.digitoVerificador = digitoVerificador.trim();
        this.titular = titular;
        this.tipo = tipo;
        this.status = StatusConta.ATIVA;
        this.saldo = BigDecimal.ZERO;
    }

    private static void validaCriacaoConta(NumeroConta numero, String digitoVerificador, Cooperado titular, TipoConta tipo) {
        if (numero == null) throw new ValidationException("numero é obrigatório");
        if (digitoVerificador == null || digitoVerificador.trim().isEmpty())
            throw new ValidationException("digito verificador é obrigatório");
        if (titular == null) throw new ValidationException("titular (Cooperado) é obrigatório");
        if (tipo == null) throw new ValidationException("tipo de conta é obrigatório");
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

    public Cooperado getTitular() {
        return titular;
    }

    public TipoConta getTipo() {
        return tipo;
    }

    public StatusConta getStatus() {
        return status;
    }

    public BigDecimal getSaldo() {
        return saldo;
    }

    public void bloquear() {
        this.status = StatusConta.BLOQUEADA;
    }

    public void ativar() {
        this.status = StatusConta.ATIVA;
    }

    public void creditar(BigDecimal valor) {
        if (valor == null || valor.signum() <= 0) throw new ValidationException("valor deve ser positivo");
        if (this.status != StatusConta.ATIVA) throw new ValidationException("conta não está ativa");
        this.saldo = this.saldo.add(valor);
    }

    public void debitar(BigDecimal valor) {
        if (valor == null || valor.signum() <= 0) throw new ValidationException("valor deve ser positivo");
        if (this.status != StatusConta.ATIVA) throw new ValidationException("conta não está ativa");
        if (!temSaldoSuficiente(valor)) throw new ValidationException("saldo insuficiente");
        this.saldo = this.saldo.subtract(valor);
    }

    public boolean temSaldoSuficiente(BigDecimal valor) {
        if (valor == null || valor.signum() <= 0) return false;
        // Política de saldo: saldo >= valor (pode ser extendida para cheque especial)
        return this.saldo.compareTo(valor) >= 0;
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