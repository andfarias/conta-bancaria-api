package com.andersonfariasdev.contabancariaapi.domain;

import com.andersonfariasdev.contabancariaapi.domain.model.Transacao;
import com.andersonfariasdev.contabancariaapi.domain.model.enums.StatusTransacao;
import com.andersonfariasdev.contabancariaapi.domain.model.enums.TipoTransacao;
import org.junit.jupiter.api.Test;

import java.math.BigDecimal;
import java.time.OffsetDateTime;

import static org.junit.jupiter.api.Assertions.assertThrows;

class TransacaoTest {

    private static final OffsetDateTime AGORA = OffsetDateTime.now();

    @Test
    void valorNuloOuNaoPositivoLancaIllegalArgumentException() {
        assertThrows(IllegalArgumentException.class,
                () -> new Transacao(null, 1L, null, TipoTransacao.DEPOSITO, StatusTransacao.CONCLUIDA, AGORA));
        assertThrows(IllegalArgumentException.class,
                () -> new Transacao(null, 1L, BigDecimal.ZERO, TipoTransacao.DEPOSITO, StatusTransacao.CONCLUIDA, AGORA));
        assertThrows(IllegalArgumentException.class,
                () -> new Transacao(null, 1L, new BigDecimal("-1"), TipoTransacao.DEPOSITO, StatusTransacao.CONCLUIDA, AGORA));
    }

    @Test
    void tipoStatusDataObrigatorios() {
        assertThrows(IllegalArgumentException.class,
                () -> new Transacao(null, 1L, BigDecimal.ONE, null, StatusTransacao.CONCLUIDA, AGORA));
        assertThrows(IllegalArgumentException.class,
                () -> new Transacao(null, 1L, BigDecimal.ONE, TipoTransacao.DEPOSITO, null, AGORA));
        assertThrows(IllegalArgumentException.class,
                () -> new Transacao(null, 1L, BigDecimal.ONE, TipoTransacao.DEPOSITO, StatusTransacao.CONCLUIDA, null));
    }
}
