package com.andersonfariasdev.contabancariaapi.infrastructure.mapper;

import com.andersonfariasdev.contabancariaapi.adapters.inbound.dto.ClienteRequest;
import com.andersonfariasdev.contabancariaapi.adapters.outbound.entities.ClienteJpaEntity;
import com.andersonfariasdev.contabancariaapi.domain.model.Cliente;
import com.andersonfariasdev.contabancariaapi.domain.model.enums.TipoPessoa;
import com.andersonfariasdev.contabancariaapi.domain.model.value.Documento;
import com.andersonfariasdev.contabancariaapi.infrastructure.exception.ValidationException;

import java.util.Arrays;

public final class ClienteMapper {

    private ClienteMapper() {
    }

    public static Cliente toDomain(ClienteJpaEntity e) {
        if (e == null) return null;
        Documento doc = null;
        if (e.getDocumento() != null) {
            doc = new Documento(e.getDocumento());
        }
        return new Cliente(e.getId(), e.getNomeRazao(), doc, e.getTipo());
    }

    public static ClienteJpaEntity toEntity(Cliente c) {
        if (c == null) return null;
        ClienteJpaEntity e = new ClienteJpaEntity();
        e.setId(c.getId());
        e.setNomeRazao(c.getNomeRazao());
        if (c.getDocumento() != null) e.setDocumento(c.getDocumento().getValor());
        e.setTipo(c.getTipo());
        return e;
    }

    public static Cliente fromDtoToDomain(ClienteRequest clienteRequest) {
        Documento doc = null;
        if (clienteRequest.documento() != null) {
            doc = new Documento(clienteRequest.documento());
        }

        var clienteType = Arrays.stream(TipoPessoa.values())
                .filter(c -> c.name().equalsIgnoreCase(clienteRequest.tipo()))
                .findFirst()
                .orElseThrow(() -> new ValidationException("Tipo de cliente deve ser PF ou PJ"));

        return new Cliente(null, clienteRequest.nomeRazao(), doc, clienteType);
    }
}
