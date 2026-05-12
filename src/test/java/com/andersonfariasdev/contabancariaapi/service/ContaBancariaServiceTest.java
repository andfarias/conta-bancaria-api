package com.andersonfariasdev.contabancariaapi.service;

import com.andersonfariasdev.contabancariaapi.domain.ContaBancaria;
import com.andersonfariasdev.contabancariaapi.dto.ValorTransacaoRequest;
import com.andersonfariasdev.contabancariaapi.repository.ContaBancariaRepository;
import com.andersonfariasdev.contabancariaapi.repository.TransacaoRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
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

    @InjectMocks
    private ContaBancariaService service;

    @BeforeEach
    void setup() {
        MockitoAnnotations.openMocks(this);
    }

    @Test
    void depositoSimples() {
        var conta = ContaBancaria.builder().id(1L).numero("123").digitoVerificador("1").documento("111").saldo(BigDecimal.ZERO).build();
        when(contaRepo.findByNumeroForUpdate("123")).thenReturn(Optional.of(conta));
        when(contaRepo.save(any())).thenAnswer(i -> i.getArgument(0));

        var req = new ValorTransacaoRequest();
        req.setNumeroConta("123");
        req.setValor(new BigDecimal("100.00"));

        service.depositar(req);

        assertEquals(new BigDecimal("100.00"), conta.getSaldo());
        verify(transRepo, times(1)).save(any());
    }
}
