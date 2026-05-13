package com.andersonfariasdev.contabancariaapi.domain;

import com.andersonfariasdev.contabancariaapi.domain.model.Cliente;
import com.andersonfariasdev.contabancariaapi.domain.model.enums.TipoPessoa;
import com.andersonfariasdev.contabancariaapi.domain.model.value.Documento;
import com.andersonfariasdev.contabancariaapi.infrastructure.exception.ValidationException;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertThrows;

class ClienteTest {

    private static final Documento DOC = new Documento("613.443.940-19");

    @Test
    void nomeRazaoObrigatorio() {
        assertThrows(ValidationException.class, () -> new Cliente(null, null, DOC, TipoPessoa.PF));
        assertThrows(ValidationException.class, () -> new Cliente(null, "", DOC, TipoPessoa.PF));
        assertThrows(ValidationException.class, () -> new Cliente(null, "   ", DOC, TipoPessoa.PF));
    }

    @Test
    void documentoObrigatorio() {
        assertThrows(ValidationException.class, () -> new Cliente(null, "Nome", null, TipoPessoa.PF));
    }

    @Test
    void tipoObrigatorio() {
        assertThrows(ValidationException.class, () -> new Cliente(null, "Nome", DOC, null));
    }
}
