package com.andersonfariasdev.contabancariaapi.domain;

import com.andersonfariasdev.contabancariaapi.domain.model.ContaBancaria;
import com.andersonfariasdev.contabancariaapi.domain.model.Cooperado;
import com.andersonfariasdev.contabancariaapi.domain.model.enums.CooperadoType;
import com.andersonfariasdev.contabancariaapi.domain.model.enums.StatusConta;
import com.andersonfariasdev.contabancariaapi.domain.model.enums.TipoConta;
import com.andersonfariasdev.contabancariaapi.domain.model.value.Documento;
import com.andersonfariasdev.contabancariaapi.domain.model.value.NumeroConta;
import com.andersonfariasdev.contabancariaapi.infrastructure.exception.ValidationException;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.math.BigDecimal;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;

class ContaBancariaTest {

    private Cooperado titular;

    @BeforeEach
    void setup() {
        titular = new Cooperado(1L, "Titular", new Documento("613.443.940-19"), CooperadoType.PF);
    }

    @Test
    void construtorNumeroNulo() {
        assertThrows(ValidationException.class,
                () -> new ContaBancaria(1L, null, "1", titular, TipoConta.CORRENTE));
    }

    @Test
    void construtorDigitoObrigatorio() {
        assertThrows(ValidationException.class,
                () -> new ContaBancaria(1L, new NumeroConta("1"), null, titular, TipoConta.CORRENTE));
        assertThrows(ValidationException.class,
                () -> new ContaBancaria(1L, new NumeroConta("1"), "  ", titular, TipoConta.CORRENTE));
    }

    @Test
    void construtorTitularObrigatorio() {
        assertThrows(ValidationException.class,
                () -> new ContaBancaria(1L, new NumeroConta("1"), "1", null, TipoConta.CORRENTE));
    }

    @Test
    void construtorTipoObrigatorio() {
        assertThrows(ValidationException.class,
                () -> new ContaBancaria(1L, new NumeroConta("1"), "1", titular, null));
    }

    @Test
    void creditarValorInvalidoOuContaInativa() {
        var conta = new ContaBancaria(1L, new NumeroConta("1"), "1", titular, TipoConta.CORRENTE);
        assertThrows(ValidationException.class, () -> conta.creditar(null));
        assertThrows(ValidationException.class, () -> conta.creditar(BigDecimal.ZERO));
        conta.bloquear();
        assertThrows(ValidationException.class, () -> conta.creditar(BigDecimal.ONE));
    }

    @Test
    void debitarSaldoInsuficienteOuContaInativa() {
        var conta = new ContaBancaria(1L, new NumeroConta("1"), "1", titular, TipoConta.CORRENTE);
        assertThrows(ValidationException.class, () -> conta.debitar(new BigDecimal("0.01")));
        conta.creditar(new BigDecimal("50.00"));
        conta.debitar(new BigDecimal("25.00"));
        assertEquals(new BigDecimal("25.00"), conta.getSaldo());
        assertThrows(ValidationException.class, () -> conta.debitar(new BigDecimal("30.00")));
    }

    @Test
    void temSaldoSuficienteRespeitaPolitica() {
        var conta = new ContaBancaria(1L, new NumeroConta("1"), "1", titular, TipoConta.CORRENTE);
        conta.creditar(new BigDecimal("100.00"));
        assertEquals(true, conta.temSaldoSuficiente(new BigDecimal("100.00")));
        assertEquals(false, conta.temSaldoSuficiente(new BigDecimal("100.01")));
    }
}
