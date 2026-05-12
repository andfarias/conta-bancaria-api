package com.andersonfariasdev.contabancariaapi.infrastructure.mapper;

import com.andersonfariasdev.contabancariaapi.adapters.outbound.entities.ContaBancariaJpaEntity;
import com.andersonfariasdev.contabancariaapi.adapters.outbound.entities.TransacaoJpaEntity;
import com.andersonfariasdev.contabancariaapi.domain.model.Transacao;

public final class TransacaoMapper {

    private TransacaoMapper() {
    }

    public static Transacao toDomain(TransacaoJpaEntity e) {
        if (e == null) return null;
        return new Transacao(e.getId(), e.getContaBancaria().getId(), e.getValor(), e.getTipo(), e.getOcorridoEm(), e.getMetadados());
    }

    public static TransacaoJpaEntity toEntity(Transacao t, ContaBancariaJpaEntity contaEntity) {
        if (t == null) return null;
        TransacaoJpaEntity e = new TransacaoJpaEntity();
        e.setId(t.getId());
        e.setContaBancaria(contaEntity);
        e.setValor(t.getValor());
        e.setTipo(t.getTipo());
        e.setOcorridoEm(t.getOcorridoEm());
        e.setMetadados(t.getMetadados());
        return e;
    }
}
