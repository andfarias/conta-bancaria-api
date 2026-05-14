package com.andersonfariasdev.contabancariaapi.domain;

import com.andersonfariasdev.contabancariaapi.domain.model.value.IdentificadorConta;
import com.andersonfariasdev.contabancariaapi.infrastructure.exception.ValidationException;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;

class IdentificadorContaTest {

    @Test
    void criacaoSucesso() {
        var id = new IdentificadorConta("0001", "123456", "7");
        assertEquals("0001", id.agencia());
        assertEquals("123456", id.conta());
        assertEquals("7", id.digito());
        assertEquals("0001-123456-7", id.getFullNumber());
    }

    @Test
    void criacaoComAgenciaDefault() {
        var id = new IdentificadorConta("123456", "7");
        assertEquals("0001", id.agencia());
        assertEquals("123456", id.conta());
        assertEquals("7", id.digito());
        assertEquals("0001-123456-7", id.getFullNumber());
    }

    @Test
    void criacaoComAgenciaNulaUsaDefault() {
        var id = new IdentificadorConta(null, "123456", "7");
        assertEquals("0001", id.agencia());
    }

    @Test
    void contaInvalidaLancaException() {
        assertThrows(ValidationException.class, () -> new IdentificadorConta("123", "7"));
        assertThrows(ValidationException.class, () -> new IdentificadorConta("abcdef", "7"));
        assertThrows(ValidationException.class, () -> new IdentificadorConta(null, "7"));
    }

    @Test
    void digitoInvalidoLancaException() {
        assertThrows(ValidationException.class, () -> new IdentificadorConta("123456", "10"));
        assertThrows(ValidationException.class, () -> new IdentificadorConta("123456", "Y"));
        assertThrows(ValidationException.class, () -> new IdentificadorConta("123456", null));
    }

    @Test
    void digitoXouXValido() {
        var id1 = new IdentificadorConta("123456", "X");
        var id2 = new IdentificadorConta("123456", "x");
        assertEquals("X", id1.digito());
        assertEquals("x", id2.digito());
    }
}
