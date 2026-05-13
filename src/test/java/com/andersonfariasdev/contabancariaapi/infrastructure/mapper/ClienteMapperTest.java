package com.andersonfariasdev.contabancariaapi.infrastructure.mapper;

import com.andersonfariasdev.contabancariaapi.adapters.inbound.dto.ClienteRequest;
import com.andersonfariasdev.contabancariaapi.domain.model.enums.TipoPessoa;
import com.andersonfariasdev.contabancariaapi.infrastructure.exception.ValidationException;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;

class ClienteMapperTest {

    @Test
    void fromDtoTipoInvalidoLancaValidationException() {
        var req = new ClienteRequest("Nome", "613.443.940-19", "MEI");
        assertThrows(ValidationException.class, () -> ClienteMapper.fromDtoToDomain(req));
    }

    @Test
    void fromDtoAceitaPfPjCaseInsensitive() {
        var pf = ClienteMapper.fromDtoToDomain(new ClienteRequest("A", "613.443.940-19", "pf"));
        assertEquals(TipoPessoa.PF, pf.getTipo());

        var pj = ClienteMapper.fromDtoToDomain(new ClienteRequest(
                "Empresa", "11.222.333/0001-81", "pj"));
        assertEquals(TipoPessoa.PJ, pj.getTipo());
    }
}
