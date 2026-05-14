package com.andersonfariasdev.contabancariaapi.infrastructure.mapper;

import com.andersonfariasdev.contabancariaapi.adapters.outbound.entities.ContaBancariaJpaEntity;
import com.andersonfariasdev.contabancariaapi.domain.model.ContaBancaria;
import com.andersonfariasdev.contabancariaapi.domain.model.value.IdentificadorConta;

public final class ContaMapper {

    private ContaMapper() {
    }

    public static ContaBancaria toDomain(ContaBancariaJpaEntity e) {
        if (e == null) return null;
        var titularDomain = ClienteMapper.toDomain(e.getTitular());
        
        return new ContaBancaria(
                e.getId(),
                new IdentificadorConta(e.getAgencia(), e.getNumero(), e.getDigitoVerificador()),
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
        e.setAgencia(c.getIdentificador().agencia());
        e.setNumero(c.getIdentificador().conta());
        e.setDigitoVerificador(c.getIdentificador().digito());
        if (c.getTitular() != null) e.setTitular(ClienteMapper.toEntity(c.getTitular()));
        e.setTipo(c.getTipo());
        e.setStatus(c.getStatus());
        e.setSaldo(c.getSaldo());
        e.setVersion(c.getVersion());
        return e;
    }
}
