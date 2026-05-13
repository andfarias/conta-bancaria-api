package com.andersonfariasdev.contabancariaapi.infrastructure.mapper;

import com.andersonfariasdev.contabancariaapi.adapters.inbound.dto.CooperadoRequest;
import com.andersonfariasdev.contabancariaapi.domain.model.enums.CooperadoType;
import com.andersonfariasdev.contabancariaapi.infrastructure.exception.ValidationException;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;

class CooperadoMapperTest {

    @Test
    void fromDtoTipoInvalidoLancaValidationException() {
        var req = new CooperadoRequest("Nome", "613.443.940-19", "MEI");
        assertThrows(ValidationException.class, () -> CooperadoMapper.fromDtoToDomain(req));
    }

    @Test
    void fromDtoAceitaPfPjCaseInsensitive() {
        var pf = CooperadoMapper.fromDtoToDomain(new CooperadoRequest("A", "613.443.940-19", "pf"));
        assertEquals(CooperadoType.PF, pf.getTipo());

        var pj = CooperadoMapper.fromDtoToDomain(new CooperadoRequest(
                "Empresa", "11.222.333/0001-81", "pj"));
        assertEquals(CooperadoType.PJ, pj.getTipo());
    }
}
