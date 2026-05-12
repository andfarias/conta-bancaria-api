package com.andersonfariasdev.contabancariaapi.domain.model;

import com.andersonfariasdev.contabancariaapi.infrastructure.exception.ValidationException;

import java.math.BigDecimal;
import java.time.OffsetDateTime;
import java.util.Objects;

public class Transacao {

    private final Long id;
    private final Long contaId;
    private final BigDecimal valor;
    private final String tipo;
    private final OffsetDateTime ocorridoEm;
    private final String metadados;

    public Transacao(Long id, Long contaId, BigDecimal valor, String tipo, OffsetDateTime ocorridoEm, String metadados) {
        if (contaId == null) throw new ValidationException("contaId é obrigatório");
        if (valor == null || valor.signum() <= 0) throw new ValidationException("valor deve ser positivo");
        if (tipo == null || tipo.trim().isEmpty()) throw new ValidationException("tipo é obrigatório");
        this.id = id;
        this.contaId = contaId;
        this.valor = valor;
        this.tipo = tipo;
        this.ocorridoEm = ocorridoEm == null ? OffsetDateTime.now() : ocorridoEm;
        this.metadados = metadados;
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

    public String getTipo() {
        return tipo;
    }

    public OffsetDateTime getOcorridoEm() {
        return ocorridoEm;
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
