package com.andersonfariasdev.contabancariaapi.infrastructure.mapper;

import com.andersonfariasdev.contabancariaapi.adapters.outbound.entities.ContaBancariaJpaEntity;
import com.andersonfariasdev.contabancariaapi.domain.model.ContaBancaria;
import com.andersonfariasdev.contabancariaapi.domain.model.value.NumeroConta;

public final class ContaMapper {

    private ContaMapper() {
    }

    public static ContaBancaria toDomain(ContaBancariaJpaEntity e) {
        if (e == null) return null;
        return new ContaBancaria(e.getId(), new NumeroConta(e.getNumero()), e.getDigitoVerificador(), e.getDocumento(), e.getSaldo());
    }

    public static ContaBancariaJpaEntity toEntity(ContaBancaria c) {
        if (c == null) return null;
        ContaBancariaJpaEntity e = new ContaBancariaJpaEntity();
        e.setId(c.getId());
        e.setNumero(c.getNumero().getValor());
        e.setDigitoVerificador(c.getDigitoVerificador());
        e.setDocumento(c.getDocumento());
        e.setSaldo(c.getSaldo());
        return e;
    }
}
