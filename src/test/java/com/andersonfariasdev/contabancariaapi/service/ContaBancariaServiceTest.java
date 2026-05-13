package com.andersonfariasdev.contabancariaapi.service;

import com.andersonfariasdev.contabancariaapi.adapters.inbound.dto.ValorTransacaoRequest;
import com.andersonfariasdev.contabancariaapi.application.service.ContaBancariaService;
import com.andersonfariasdev.contabancariaapi.domain.model.ContaBancaria;
import com.andersonfariasdev.contabancariaapi.domain.model.Cooperado;
import com.andersonfariasdev.contabancariaapi.domain.model.enums.CooperadoType;
import com.andersonfariasdev.contabancariaapi.domain.model.enums.StatusConta;
import com.andersonfariasdev.contabancariaapi.domain.model.enums.TipoConta;
import com.andersonfariasdev.contabancariaapi.domain.model.value.Documento;
import com.andersonfariasdev.contabancariaapi.domain.model.value.NumeroConta;
import com.andersonfariasdev.contabancariaapi.domain.repository.ContaBancariaRepository;
import com.andersonfariasdev.contabancariaapi.domain.repository.CooperadoRepository;
import com.andersonfariasdev.contabancariaapi.domain.repository.TransacaoRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;

import java.math.BigDecimal;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

class ContaBancariaServiceTest {

    @Mock
    private ContaBancariaRepository contaRepo;

    @Mock
    private TransacaoRepository transRepo;

    @Mock
    private CooperadoRepository cooperadoRepo;

    private ContaBancariaService service;

    @BeforeEach
    void setup() {
        MockitoAnnotations.openMocks(this);
        service = new ContaBancariaService(contaRepo, transRepo, cooperadoRepo);
    }

    @Test
    void depositoSimples() {
        var conta = new ContaBancaria(1L, new NumeroConta("123"), "1", new Cooperado(1L, "TEST", new Documento("613.443.940-19"), CooperadoType.PF), TipoConta.CORRENTE);
        when(contaRepo.findByNumeroForUpdate("123")).thenReturn(Optional.of(conta));
        when(contaRepo.save(any())).thenAnswer(i -> i.getArgument(0));

        var req = new ValorTransacaoRequest("123", new BigDecimal("100.00"));

        service.depositar(req.numeroConta(), req.valor());

        assertEquals(new BigDecimal("100.00"), conta.getSaldo());
        verify(transRepo, times(1)).save(any());
    }
}
