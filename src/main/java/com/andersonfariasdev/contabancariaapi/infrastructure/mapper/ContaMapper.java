package com.andersonfariasdev.contabancariaapi.infrastructure.mapper;

import com.andersonfariasdev.contabancariaapi.adapters.outbound.entities.ContaBancariaJpaEntity;
import com.andersonfariasdev.contabancariaapi.domain.model.ContaBancaria;
import com.andersonfariasdev.contabancariaapi.domain.model.value.NumeroConta;
import com.andersonfariasdev.contabancariaapi.infrastructure.mapper.ClienteMapper;

public final class ContaMapper {

    private ContaMapper() {
    }

    public static ContaBancaria toDomain(ContaBancariaJpaEntity e) {
        if (e == null) return null;
        // Some legacy tests or fixtures may create Conta entities without a Cliente (titular null).
        // Ensure we provide a valid Cliente domain object constructed from the documento column when titular is missing.
        var titularDomain = ClienteMapper.toDomain(e.getTitular());
        
        return new ContaBancaria(
                e.getId(),
                new NumeroConta(e.getNumero()),
                e.getDigitoVerificador(),
                titularDomain,
                e.getTipo(),
                e.getStatus(),
                e.getSaldo(),
                e.getVersion()
        );
    }

    public static ContaBancariaJpaEntity toEntity(ContaBancaria c) {
        if (c == null) return null;
        ContaBancariaJpaEntity e = new ContaBancariaJpaEntity();
        e.setId(c.getId());
        e.setNumero(c.getNumero().getValor());
        e.setDigitoVerificador(c.getDigitoVerificador());
        if (c.getTitular() != null) e.setTitular(ClienteMapper.toEntity(c.getTitular()));
        e.setTipo(c.getTipo());
        e.setStatus(c.getStatus());
        e.setSaldo(c.getSaldo());
        e.setVersion(c.getVersion());
        return e;
    }
}
