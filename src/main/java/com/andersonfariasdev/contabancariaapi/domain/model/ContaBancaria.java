package com.andersonfariasdev.contabancariaapi.domain.model;

import com.andersonfariasdev.contabancariaapi.domain.model.enums.StatusConta;
import com.andersonfariasdev.contabancariaapi.domain.model.enums.TipoConta;
import com.andersonfariasdev.contabancariaapi.domain.model.value.IdentificadorConta;
import com.andersonfariasdev.contabancariaapi.infrastructure.exception.ValidationException;

import java.math.BigDecimal;
import java.util.Objects;

public class ContaBancaria {

    private final Long id;
    private final IdentificadorConta identificador;
    private final Cliente titular;
    private final TipoConta tipo;
    private final Long version;
    private StatusConta status;
    private BigDecimal saldo;

    public ContaBancaria(Long id, IdentificadorConta identificador, Cliente titular, TipoConta tipo, StatusConta status, BigDecimal saldo) {
        this(id, identificador, titular, tipo, status, saldo, null);
    }

    public ContaBancaria(Long id, IdentificadorConta identificador, Cliente titular, TipoConta tipo, StatusConta status, BigDecimal saldo, Long version) {
        validaCriacaoConta(identificador, titular, tipo);
        if (status == null) status = StatusConta.ATIVA;
        this.id = id;
        this.identificador = identificador;
        this.titular = titular;
        this.tipo = tipo;
        this.status = status;
        this.saldo = saldo == null ? BigDecimal.ZERO : saldo;
        this.version = version;
    }

    public ContaBancaria(Long id, IdentificadorConta identificador, Cliente titular, TipoConta tipo) {
        this(id, identificador, titular, tipo, StatusConta.ATIVA, BigDecimal.ZERO, null);
    }

    private static void validaCriacaoConta(IdentificadorConta identificador, Cliente titular, TipoConta tipo) {
        if (identificador == null) throw new ValidationException("identificador da conta é obrigatório");
        if (titular == null) throw new ValidationException("titular (Cliente) é obrigatório");
        if (tipo == null) throw new ValidationException("tipo de conta é obrigatório");
    }

    public Long getId() {
        return id;
    }

    public IdentificadorConta getIdentificador() {
        return identificador;
    }

    public Cliente getTitular() {
        return titular;
    }

    public TipoConta getTipo() {
        return tipo;
    }

    /**
     * Versão de concorrência otimista (espelha {@code @Version} na persistência). {@code null} em contas novas.
     */
    public Long getVersion() {
        return version;
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
        return this.saldo.compareTo(valor) >= 0;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        ContaBancaria that = (ContaBancaria) o;
        return Objects.equals(id, that.id) && Objects.equals(identificador, that.identificador);
    }

    @Override
    public int hashCode() {
        return Objects.hash(id, identificador);
    }

    public String getAgencia() {
        return identificador.agencia();
    }

    public String getNumero() {
        return identificador.conta();
    }

    public String getDigitoVerificador() {
        return identificador.digito();
    }
}
