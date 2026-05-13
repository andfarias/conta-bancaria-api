package com.andersonfariasdev.contabancariaapi.domain;

import com.andersonfariasdev.contabancariaapi.domain.model.value.NumeroConta;
import com.andersonfariasdev.contabancariaapi.infrastructure.exception.ValidationException;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;

class NumeroContaTest {

    @Test
    void nuloLancaValidationException() {
        assertThrows(ValidationException.class, () -> new NumeroConta(null));
    }

    @Test
    void vazioOuBrancoLancaValidationException() {
        assertThrows(ValidationException.class, () -> new NumeroConta(""));
        assertThrows(ValidationException.class, () -> new NumeroConta("   "));
    }

    @Test
    void trimNoValor() {
        assertEquals("12345", new NumeroConta("  12345  ").getValor());
    }
}
