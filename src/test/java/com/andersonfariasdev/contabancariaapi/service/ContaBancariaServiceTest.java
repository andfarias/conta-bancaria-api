package com.andersonfariasdev.contabancariaapi.service;

import com.andersonfariasdev.contabancariaapi.adapters.inbound.dto.ContaBancariaCriacaoRequest;
import com.andersonfariasdev.contabancariaapi.adapters.inbound.dto.TransferenciaRequest;
import com.andersonfariasdev.contabancariaapi.application.service.ContaBancariaService;
import com.andersonfariasdev.contabancariaapi.domain.model.ContaBancaria;
import com.andersonfariasdev.contabancariaapi.domain.model.Cooperado;
import com.andersonfariasdev.contabancariaapi.domain.model.enums.CooperadoType;
import com.andersonfariasdev.contabancariaapi.domain.model.enums.TipoConta;
import com.andersonfariasdev.contabancariaapi.domain.model.value.Documento;
import com.andersonfariasdev.contabancariaapi.domain.model.value.NumeroConta;
import com.andersonfariasdev.contabancariaapi.domain.repository.ContaBancariaRepository;
import com.andersonfariasdev.contabancariaapi.domain.repository.CooperadoRepository;
import com.andersonfariasdev.contabancariaapi.domain.repository.TransacaoRepository;
import com.andersonfariasdev.contabancariaapi.infrastructure.exception.ContaNaoEncontradaException;
import com.andersonfariasdev.contabancariaapi.infrastructure.exception.SaldoInsuficienteException;
import com.andersonfariasdev.contabancariaapi.infrastructure.exception.ValidationException;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;

import java.math.BigDecimal;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

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
        var conta = contaAtiva("123", new BigDecimal("0.00"));
        when(contaRepo.findByNumeroForUpdate("123")).thenReturn(Optional.of(conta));
        when(contaRepo.save(any())).thenAnswer(i -> i.getArgument(0));

        service.depositar("123", new BigDecimal("100.00"));

        assertEquals(new BigDecimal("100.00"), conta.getSaldo());
        verify(transRepo).save(any());
    }

    @Test
    void depositoContaInexistenteLancaContaNaoEncontrada() {
        when(contaRepo.findByNumeroForUpdate("404")).thenReturn(Optional.empty());
        assertThrows(ContaNaoEncontradaException.class, () -> service.depositar("404", BigDecimal.ONE));
        verify(transRepo, never()).save(any());
    }

    @Test
    void saqueSaldoInsuficiente() {
        var conta = contaAtiva("9", new BigDecimal("5.00"));
        when(contaRepo.findByNumeroForUpdate("9")).thenReturn(Optional.of(conta));
        assertThrows(SaldoInsuficienteException.class, () -> service.sacar("9", new BigDecimal("10.00")));
        verify(contaRepo, never()).save(any());
        verify(transRepo, never()).save(any());
    }

    @Test
    void saqueSucesso() {
        var conta = contaAtiva("9", new BigDecimal("50.00"));
        when(contaRepo.findByNumeroForUpdate("9")).thenReturn(Optional.of(conta));
        when(contaRepo.save(any())).thenAnswer(i -> i.getArgument(0));

        service.sacar("9", new BigDecimal("20.00"));

        assertEquals(new BigDecimal("30.00"), conta.getSaldo());
        verify(transRepo).save(any());
    }

    @Test
    void transferenciaOrigemSemSaldo() {
        var a = contaAtiva("A", new BigDecimal("5.00"));
        var b = contaAtiva("B", new BigDecimal("0.00"));
        when(contaRepo.findByNumeroForUpdate("A")).thenReturn(Optional.of(a));
        when(contaRepo.findByNumeroForUpdate("B")).thenReturn(Optional.of(b));

        var req = new TransferenciaRequest("A", "B", new BigDecimal("10.00"));
        assertThrows(SaldoInsuficienteException.class, () -> service.transferir(req));
        verify(contaRepo, never()).save(any());
    }

    @Test
    void transferenciaContaDestinoInexistente() {
        var a = contaAtiva("A", new BigDecimal("100.00"));
        when(contaRepo.findByNumeroForUpdate("A")).thenReturn(Optional.of(a));
        when(contaRepo.findByNumeroForUpdate("Z")).thenReturn(Optional.empty());

        var req = new TransferenciaRequest("A", "Z", BigDecimal.ONE);
        assertThrows(ContaNaoEncontradaException.class, () -> service.transferir(req));
    }

    @Test
    void transferenciaAtualizaSaldos() {
        var a = contaAtiva("A", new BigDecimal("100.00"));
        var b = contaAtiva("B", new BigDecimal("50.00"));
        when(contaRepo.findByNumeroForUpdate("A")).thenReturn(Optional.of(a));
        when(contaRepo.findByNumeroForUpdate("B")).thenReturn(Optional.of(b));
        when(contaRepo.save(any())).thenAnswer(i -> i.getArgument(0));

        service.transferir(new TransferenciaRequest("A", "B", new BigDecimal("25.00")));

        assertEquals(new BigDecimal("75.00"), a.getSaldo());
        assertEquals(new BigDecimal("75.00"), b.getSaldo());
        verify(transRepo, times(2)).save(any());
    }

    @Test
    void transferenciaMesmaContaNaoAlteraSaldoLiquido() {
        var a = contaAtiva("A", new BigDecimal("200.00"));
        when(contaRepo.findByNumeroForUpdate("A")).thenReturn(Optional.of(a));
        when(contaRepo.save(any())).thenAnswer(i -> i.getArgument(0));

        service.transferir(new TransferenciaRequest("A", "A", new BigDecimal("40.00")));

        assertEquals(new BigDecimal("200.00"), a.getSaldo());
        verify(transRepo, times(2)).save(any());
    }

    @Test
    void criarContaPorCooperadoIdCooperadoInexistente() {
        when(cooperadoRepo.findById(99L)).thenReturn(Optional.empty());
        var req = new ContaBancariaCriacaoRequest("1", "0", "CORRENTE", 99L);
        assertThrows(ValidationException.class, () -> service.criarConta(99L, req));
        verify(contaRepo, never()).save(any());
    }

    @Test
    void criarContaPorCooperadoIdTipoInvalido() {
        var coop = new Cooperado(1L, "X", new Documento("613.443.940-19"), CooperadoType.PF);
        when(cooperadoRepo.findById(1L)).thenReturn(Optional.of(coop));
        var req = new ContaBancariaCriacaoRequest("99", "1", "INVESTIMENTO", 1L);
        assertThrows(ValidationException.class, () -> service.criarConta(1L, req));
    }

    @Test
    void criarContaPorCooperadoIdNumeroJaExiste() {
        var coop = new Cooperado(1L, "X", new Documento("613.443.940-19"), CooperadoType.PF);
        when(cooperadoRepo.findById(1L)).thenReturn(Optional.of(coop));
        when(contaRepo.findByNumero("dup")).thenReturn(Optional.of(contaAtiva("dup", BigDecimal.ZERO)));

        var req = new ContaBancariaCriacaoRequest("dup", "1", "poupanca", 1L);
        assertThrows(ValidationException.class, () -> service.criarConta(1L, req));
        verify(contaRepo, never()).save(any());
    }

    @Test
    void criarContaPorCooperadoIdSucesso() {
        var coop = new Cooperado(1L, "X", new Documento("613.443.940-19"), CooperadoType.PF);
        when(cooperadoRepo.findById(1L)).thenReturn(Optional.of(coop));
        when(contaRepo.findByNumero("777")).thenReturn(Optional.empty());
        when(contaRepo.save(any())).thenAnswer(i -> i.getArgument(0));

        var criada = service.criarConta(1L, new ContaBancariaCriacaoRequest("777", "2", "POUPANCA", 1L));

        assertEquals(TipoConta.POUPANCA, criada.getTipo());
        assertEquals("777", criada.getNumero().getValor());
        verify(contaRepo).save(any());
    }

    @Test
    void criarContaDominioNumeroNuloLancaValidationException() {
        ContaBancaria contaMock = mock(ContaBancaria.class);
        when(contaMock.getNumero()).thenReturn(null);
        when(contaMock.getDigitoVerificador()).thenReturn("1");
        assertThrows(ValidationException.class, () -> service.criarConta(contaMock));
        verify(contaRepo, never()).save(any());
    }

    private static ContaBancaria contaAtiva(String numero, BigDecimal saldo) {
        var titular = new Cooperado(1L, "T", new Documento("613.443.940-19"), CooperadoType.PF);
        return new ContaBancaria(1L, new NumeroConta(numero), "1", titular, TipoConta.CORRENTE, null, saldo);
    }
}
