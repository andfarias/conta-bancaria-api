package com.andersonfariasdev.contabancariaapi.domain.model;

import com.andersonfariasdev.contabancariaapi.domain.model.enums.TipoPessoa;
import com.andersonfariasdev.contabancariaapi.domain.model.value.Documento;
import com.andersonfariasdev.contabancariaapi.infrastructure.exception.ValidationException;

import java.util.Objects;

public class Cliente {

    private final Long id;
    private final String nomeRazao;
    private final Documento documento;
    private final TipoPessoa tipo;

    public Cliente(Long id, String nomeRazao, Documento documento, TipoPessoa tipo) {
        if (nomeRazao == null || nomeRazao.trim().isEmpty()) throw new ValidationException("nome/razao é obrigatório");
        if (documento == null) throw new ValidationException("documento é obrigatório");
        if (tipo == null) throw new ValidationException("tipo de cliente é obrigatório");
        this.id = id;
        this.nomeRazao = nomeRazao.trim();
        this.documento = documento;
        this.tipo = tipo;
    }

    public Long getId() {
        return id;
    }

    public String getNomeRazao() {
        return nomeRazao;
    }

    public Documento getDocumento() {
        return documento;
    }

    public TipoPessoa getTipo() {
        return tipo;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        Cliente cliente = (Cliente) o;
        return Objects.equals(id, cliente.id) && Objects.equals(documento, cliente.documento);
    }

    @Override
    public int hashCode() {
        return Objects.hash(id, documento);
    }
}
