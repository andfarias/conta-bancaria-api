package com.andersonfariasdev.contabancariaapi.infrastructure.mapper;

import com.andersonfariasdev.contabancariaapi.adapters.inbound.dto.CooperadoRequest;
import com.andersonfariasdev.contabancariaapi.adapters.outbound.entities.CooperadoJpaEntity;
import com.andersonfariasdev.contabancariaapi.domain.model.Cooperado;
import com.andersonfariasdev.contabancariaapi.domain.model.enums.CooperadoType;
import com.andersonfariasdev.contabancariaapi.domain.model.value.Documento;
import com.andersonfariasdev.contabancariaapi.infrastructure.exception.ValidationException;

import java.util.Arrays;

public final class CooperadoMapper {

    private CooperadoMapper() {
    }

    public static Cooperado toDomain(CooperadoJpaEntity e) {
        if (e == null) return null;
        Documento doc = null;
        if (e.getDocumento() != null) {
            doc = new Documento(e.getDocumento());
        }
        return new Cooperado(e.getId(), e.getNomeRazao(), doc, e.getTipo());
    }

    public static CooperadoJpaEntity toEntity(Cooperado c) {
        if (c == null) return null;
        CooperadoJpaEntity e = new CooperadoJpaEntity();
        e.setId(c.getId());
        e.setNomeRazao(c.getNomeRazao());
        if (c.getDocumento() != null) e.setDocumento(c.getDocumento().getValor());
        e.setTipo(c.getTipo());
        return e;
    }

    public static Cooperado fromDtoToDomain(CooperadoRequest cooperadoRequest) {
        Documento doc = null;
        if (cooperadoRequest.documento() != null) {
            doc = new Documento(cooperadoRequest.documento());
        }

        var cooperadoType = Arrays.stream(CooperadoType.values())
                .filter(c -> c.name().equalsIgnoreCase(cooperadoRequest.tipo()))
                .findFirst()
                .orElseThrow(() -> new ValidationException("Tipo de cooperado deve ser PF ou PJ"));

        return new Cooperado(null, cooperadoRequest.nomeRazao(), doc, cooperadoType);
    }
}
