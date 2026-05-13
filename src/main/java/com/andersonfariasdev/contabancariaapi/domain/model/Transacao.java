package com.andersonfariasdev.contabancariaapi.domain.model;

import com.andersonfariasdev.contabancariaapi.domain.model.enums.StatusTransacao;
import com.andersonfariasdev.contabancariaapi.domain.model.enums.TipoTransacao;

import java.math.BigDecimal;
import java.time.OffsetDateTime;
import java.util.Objects;

public final class Transacao {

    private final Long id;
    private final Long contaId;
    private final BigDecimal valor;
    private final TipoTransacao tipo;
    private final StatusTransacao status;
    private final OffsetDateTime dataHora;
    private final String metadados;

    public Transacao(Long id, Long contaId, BigDecimal valor, TipoTransacao tipo, StatusTransacao status, OffsetDateTime dataHora, String metadados) {
        validaCriacaoTransacao(valor, tipo, status, dataHora);
        this.id = id;
        this.contaId = contaId;
        this.valor = valor;
        this.tipo = tipo;
        this.status = status;
        this.dataHora = dataHora;
        this.metadados = metadados;
    }

    public Transacao(Long id, Long contaId, BigDecimal valor, TipoTransacao tipo, StatusTransacao status, OffsetDateTime dataHora) {
        validaCriacaoTransacao(valor, tipo, status, dataHora);
        this.id = id;
        this.contaId = contaId;
        this.valor = valor;
        this.tipo = tipo;
        this.status = status;
        this.dataHora = dataHora;
        this.metadados = null;
    }

    private static void validaCriacaoTransacao(BigDecimal valor, TipoTransacao tipo, StatusTransacao status, OffsetDateTime dataHora) {
        if (valor == null || valor.signum() <= 0) throw new IllegalArgumentException("valor deve ser positivo");
        if (tipo == null) throw new IllegalArgumentException("tipo é obrigatório");
        if (status == null) throw new IllegalArgumentException("status é obrigatório");
        if (dataHora == null) throw new IllegalArgumentException("dataHora é obrigatória");
    }

    public Long getId() {
        return id;
    }

    public Long getContaId() {
        return contaId;
    }

    public BigDecimal getValor() {
        return valor;
    }

    public TipoTransacao getTipo() {
        return tipo;
    }

    public StatusTransacao getStatus() {
        return status;
    }

    public OffsetDateTime getDataHora() {
        return dataHora;
    }

    public String getMetadados() {
        return metadados;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        Transacao transacao = (Transacao) o;
        return Objects.equals(id, transacao.id);
    }

    @Override
    public int hashCode() {
        return Objects.hash(id);
    }
}