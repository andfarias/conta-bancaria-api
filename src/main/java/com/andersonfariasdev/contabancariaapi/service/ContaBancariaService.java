package com.andersonfariasdev.contabancariaapi.service;

import com.andersonfariasdev.contabancariaapi.domain.ContaBancaria;
import com.andersonfariasdev.contabancariaapi.domain.Transacao;
import com.andersonfariasdev.contabancariaapi.dto.TransferenciaRequest;
import com.andersonfariasdev.contabancariaapi.dto.ValorTransacaoRequest;
import com.andersonfariasdev.contabancariaapi.exception.ContaNaoEncontradaException;
import com.andersonfariasdev.contabancariaapi.exception.SaldoInsuficienteException;
import com.andersonfariasdev.contabancariaapi.exception.ValidationException;
import com.andersonfariasdev.contabancariaapi.repository.ContaBancariaRepository;
import com.andersonfariasdev.contabancariaapi.repository.TransacaoRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.time.OffsetDateTime;

@Service
@RequiredArgsConstructor
public class ContaBancariaService {

    private final ContaBancariaRepository contaBancariaRepository;
    private final TransacaoRepository transacaoRepository;

    @Transactional
    public ContaBancaria criarConta(ContaBancaria contaBancaria) {
        // validações simples para número, dígito e documento
        if (contaBancaria.getNumero() == null || contaBancaria.getDigitoVerificador() == null) {
            throw new ValidationException("numero da conta e digito verificador são obrigatórios");
        }
        contaBancaria.setSaldo(BigDecimal.ZERO);
        return contaBancariaRepository.save(contaBancaria);
    }

    @Transactional
    public void depositar(ValorTransacaoRequest request) {
        var opt = contaBancariaRepository.findByNumeroForUpdate(request.getNumeroConta());
        if (opt.isEmpty()) throw new ContaNaoEncontradaException(request.getNumeroConta());
        var conta = opt.get();
        conta.setSaldo(conta.getSaldo().add(request.getValor()));
        contaBancariaRepository.save(conta);

        transacaoRepository.save(Transacao.builder()
                .contaBancaria(conta)
                .valor(request.getValor())
                .tipo("DEPOSITO")
                .ocorridoEm(OffsetDateTime.now())
                .build());
    }

    @Transactional
    public void sacar(ValorTransacaoRequest request) {
        var opt = contaBancariaRepository.findByNumeroForUpdate(request.getNumeroConta());
        if (opt.isEmpty()) throw new ContaNaoEncontradaException(request.getNumeroConta());
        var conta = opt.get();

        if (conta.getSaldo().compareTo(request.getValor()) < 0) {
            throw new SaldoInsuficienteException("Saldo insuficiente");
        }

        conta.setSaldo(conta.getSaldo().subtract(request.getValor()));
        contaBancariaRepository.save(conta);

        transacaoRepository.save(Transacao.builder()
                .contaBancaria(conta)
                .valor(request.getValor())
                .tipo("SAQUE")
                .ocorridoEm(OffsetDateTime.now())
                .build());
    }

    @Transactional
    public void transferir(TransferenciaRequest request) {
        // Para evitar deadlocks, travar contas em ordem consistente por número
        String a = request.getContaOrigem();
        String b = request.getContaDestino();
        boolean same = a.equals(b);

        var first = a.compareTo(b) <= 0 ? a : b;
        var second = a.compareTo(b) <= 0 ? b : a;

        var firstAccOpt = contaBancariaRepository.findByNumeroForUpdate(first);
        var secondAccOpt = same ? firstAccOpt : contaBancariaRepository.findByNumeroForUpdate(second);

        if (firstAccOpt.isEmpty()) throw new ContaNaoEncontradaException(first);
        if (secondAccOpt.isEmpty()) throw new ContaNaoEncontradaException(second);

        var origem = a.equals(first) ? firstAccOpt.get() : secondAccOpt.get();
        var destino = a.equals(first) ? secondAccOpt.get() : firstAccOpt.get();

        if (origem.getSaldo().compareTo(request.getValor()) < 0) {
            throw new SaldoInsuficienteException("Saldo insuficiente para transferência");
        }

        origem.setSaldo(origem.getSaldo().subtract(request.getValor()));
        destino.setSaldo(destino.getSaldo().add(request.getValor()));

        contaBancariaRepository.save(origem);
        if (!same) contaBancariaRepository.save(destino);

        transacaoRepository.save(Transacao.builder()
                .contaBancaria(origem)
                .valor(request.getValor())
                .tipo("TRANSFERENCIA_SAIDA")
                .ocorridoEm(OffsetDateTime.now())
                .metadados("para=" + destino.getNumero())
                .build());

        transacaoRepository.save(Transacao.builder()
                .contaBancaria(destino)
                .valor(request.getValor())
                .tipo("TRANSFERENCIA_ENTRADA")
                .ocorridoEm(OffsetDateTime.now())
                .metadados("de=" + origem.getNumero())
                .build());
    }

    @Transactional(readOnly = true)
    public Page<Transacao> extrato(Long contaBancariaId, OffsetDateTime inicio, OffsetDateTime fim, Pageable pageable) {
        if (inicio != null && fim != null) {
            return transacaoRepository.findByContaBancariaIdAndOcorridoEmBetween(contaBancariaId, inicio, fim, pageable);
        }
        return transacaoRepository.findByContaBancariaId(contaBancariaId, pageable);
    }

}
