package com.andersonfariasdev.contabancariaapi.infrastructure.mapper;

import com.andersonfariasdev.contabancariaapi.adapters.outbound.entities.ContaBancariaJpaEntity;
import com.andersonfariasdev.contabancariaapi.adapters.outbound.entities.TransacaoJpaEntity;
import com.andersonfariasdev.contabancariaapi.domain.model.Transacao;

public final class TransacaoMapper {

    private TransacaoMapper() {
    }

    public static Transacao toDomain(TransacaoJpaEntity e) {
        if (e == null) return null;
        Long origemId = e.getConta() != null ? e.getConta().getId() : null;
        return new Transacao(e.getId(), origemId, e.getValor(), e.getTipo(), e.getStatus(), e.getOcorridoEm());
    }

    public static TransacaoJpaEntity toEntity(Transacao t, ContaBancariaJpaEntity contaOrigem) {
        if (t == null) return null;
        TransacaoJpaEntity e = new TransacaoJpaEntity();
        e.setId(t.getId());
        e.setConta(contaOrigem);
        e.setValor(t.getValor());
        e.setTipo(t.getTipo());
        e.setStatus(t.getStatus());
        e.setOcorridoEm(t.getDataHora());
        e.setMetadados(t.getMetadados());
        return e;
    }
}
