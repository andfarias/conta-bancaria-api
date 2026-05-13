package com.andersonfariasdev.contabancariaapi.domain;

import com.andersonfariasdev.contabancariaapi.domain.model.enums.TipoDocumento;
import com.andersonfariasdev.contabancariaapi.domain.model.value.Documento;
import com.andersonfariasdev.contabancariaapi.infrastructure.exception.ValidationException;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.ValueSource;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;

class DocumentoTest {

    @Test
    void nuloLancaValidationException() {
        assertThrows(ValidationException.class, () -> new Documento(null));
    }

    @ParameterizedTest
    @ValueSource(strings = {"123", "123456789", "abcdefghijklmnop"})
    void tamanhoInvalidoLancaValidationException(String valor) {
        assertThrows(ValidationException.class, () -> new Documento(valor));
    }

    @Test
    void cpfInvalidoLancaValidationException() {
        assertThrows(ValidationException.class, () -> new Documento("111.111.111-11"));
    }

    @Test
    void cpfValidoNormalizaDigitos() {
        var doc = new Documento("613.443.940-19");
        assertEquals("61344394019", doc.getValor());
        assertEquals(TipoDocumento.CPF, doc.getTipo());
    }

    @Test
    void cnpjValidoNormalizaDigitos() {
        var doc = new Documento("11.222.333/0001-81");
        assertEquals("11222333000181", doc.getValor());
        assertEquals(TipoDocumento.CNPJ, doc.getTipo());
    }

    @Test
    void cnpjInvalidoLancaValidationException() {
        assertThrows(ValidationException.class, () -> new Documento("11.222.333/0001-82"));
    }
}
