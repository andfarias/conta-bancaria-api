package com.andersonfariasdev.contabancariaapi.domain;

import com.andersonfariasdev.contabancariaapi.domain.model.ContaBancaria;
import com.andersonfariasdev.contabancariaapi.domain.model.Cliente;
import com.andersonfariasdev.contabancariaapi.domain.model.enums.TipoPessoa;
import com.andersonfariasdev.contabancariaapi.domain.model.enums.TipoConta;
import com.andersonfariasdev.contabancariaapi.domain.model.value.Documento;
import com.andersonfariasdev.contabancariaapi.domain.model.value.IdentificadorConta;
import com.andersonfariasdev.contabancariaapi.infrastructure.exception.ValidationException;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.math.BigDecimal;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;

class ContaBancariaTest {

    private Cliente titular;

    @BeforeEach
    void setup() {
        titular = new Cliente(1L, "Titular", new Documento("613.443.940-19"), TipoPessoa.PF);
    }

    @Test
    void construtorIdentificadorNulo() {
        assertThrows(ValidationException.class,
                () -> new ContaBancaria(1L, null, titular, TipoConta.CORRENTE));
    }

    @Test
    void construtorTitularObrigatorio() {
        assertThrows(ValidationException.class,
                () -> new ContaBancaria(1L, new IdentificadorConta("123456", "1"), null, TipoConta.CORRENTE));
    }

    @Test
    void construtorTipoObrigatorio() {
        assertThrows(ValidationException.class,
                () -> new ContaBancaria(1L, new IdentificadorConta("123456", "1"), titular, null));
    }

    @Test
    void creditarValorInvalidoOuContaInativa() {
        var conta = new ContaBancaria(1L, new IdentificadorConta("123456", "1"), titular, TipoConta.CORRENTE);
        assertThrows(ValidationException.class, () -> conta.creditar(null));
        assertThrows(ValidationException.class, () -> conta.creditar(BigDecimal.ZERO));
        conta.bloquear();
        assertThrows(ValidationException.class, () -> conta.creditar(BigDecimal.ONE));
    }

    @Test
    void debitarSaldoInsuficienteOuContaInativa() {
        var conta = new ContaBancaria(1L, new IdentificadorConta("123456", "1"), titular, TipoConta.CORRENTE);
        assertThrows(ValidationException.class, () -> conta.debitar(new BigDecimal("0.01")));
        conta.creditar(new BigDecimal("50.00"));
        conta.debitar(new BigDecimal("25.00"));
        assertEquals(new BigDecimal("25.00"), conta.getSaldo());
        assertThrows(ValidationException.class, () -> conta.debitar(new BigDecimal("30.00")));
    }

    @Test
    void temSaldoSuficienteRespeitaPolitica() {
        var conta = new ContaBancaria(1L, new IdentificadorConta("123456", "1"), titular, TipoConta.CORRENTE);
        conta.creditar(new BigDecimal("100.00"));
        assertEquals(true, conta.temSaldoSuficiente(new BigDecimal("100.00")));
        assertEquals(false, conta.temSaldoSuficiente(new BigDecimal("100.01")));
    }
}
