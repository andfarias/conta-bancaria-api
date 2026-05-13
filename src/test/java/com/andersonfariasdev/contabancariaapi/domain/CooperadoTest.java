package com.andersonfariasdev.contabancariaapi.domain;

import com.andersonfariasdev.contabancariaapi.domain.model.Cooperado;
import com.andersonfariasdev.contabancariaapi.domain.model.enums.CooperadoType;
import com.andersonfariasdev.contabancariaapi.domain.model.value.Documento;
import com.andersonfariasdev.contabancariaapi.infrastructure.exception.ValidationException;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertThrows;

class CooperadoTest {

    private static final Documento DOC = new Documento("613.443.940-19");

    @Test
    void nomeRazaoObrigatorio() {
        assertThrows(ValidationException.class, () -> new Cooperado(null, null, DOC, CooperadoType.PF));
        assertThrows(ValidationException.class, () -> new Cooperado(null, "", DOC, CooperadoType.PF));
        assertThrows(ValidationException.class, () -> new Cooperado(null, "   ", DOC, CooperadoType.PF));
    }

    @Test
    void documentoObrigatorio() {
        assertThrows(ValidationException.class, () -> new Cooperado(null, "Nome", null, CooperadoType.PF));
    }

    @Test
    void tipoObrigatorio() {
        assertThrows(ValidationException.class, () -> new Cooperado(null, "Nome", DOC, null));
    }
}
