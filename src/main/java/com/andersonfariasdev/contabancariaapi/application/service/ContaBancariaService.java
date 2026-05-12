package com.andersonfariasdev.contabancariaapi.application.service;

import com.andersonfariasdev.contabancariaapi.adapters.inbound.dto.TransferenciaRequest;
import com.andersonfariasdev.contabancariaapi.application.usecases.ContaBancariaUseCase;
import com.andersonfariasdev.contabancariaapi.domain.model.ContaBancaria;
import com.andersonfariasdev.contabancariaapi.domain.model.Transacao;
import com.andersonfariasdev.contabancariaapi.domain.repository.ContaBancariaRepository;
import com.andersonfariasdev.contabancariaapi.domain.repository.TransacaoRepository;
import com.andersonfariasdev.contabancariaapi.infrastructure.exception.ContaNaoEncontradaException;
import com.andersonfariasdev.contabancariaapi.infrastructure.exception.SaldoInsuficienteException;
import com.andersonfariasdev.contabancariaapi.infrastructure.exception.ValidationException;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.time.OffsetDateTime;

@Service
@RequiredArgsConstructor
public class ContaBancariaService implements ContaBancariaUseCase {

    private final ContaBancariaRepository contaBancariaRepository;
    private final TransacaoRepository transacaoRepository;

    @Transactional
    public ContaBancaria criarConta(ContaBancaria contaBancaria) {
        // validações simples para número, dígito e documento
        if (contaBancaria.getNumero() == null || contaBancaria.getDigitoVerificador() == null) {
            throw new ValidationException("numero da conta e digito verificador são obrigatórios");
        }
        return contaBancariaRepository.save(contaBancaria);
    }

    @Transactional
    public void depositar(String numeroConta, BigDecimal valor) {
        var opt = contaBancariaRepository.findByNumeroForUpdate(numeroConta);
        if (opt.isEmpty()) throw new ContaNaoEncontradaException(numeroConta);
        var conta = opt.get();
        // creditar apenas o valor da transação
        conta.creditar(valor);
        contaBancariaRepository.save(conta);

        transacaoRepository.save(new Transacao(null, conta.getId(), valor, "DEPOSITO", OffsetDateTime.now(), null));
    }

    @Transactional
    public void sacar(String numeroConta, BigDecimal valor) {
        var opt = contaBancariaRepository.findByNumeroForUpdate(numeroConta);
        if (opt.isEmpty()) throw new ContaNaoEncontradaException(numeroConta);
        var conta = opt.get();

        if (conta.getSaldo().compareTo(valor) < 0) {
            throw new SaldoInsuficienteException("Saldo insuficiente");
        }

        conta.debitar(valor);
        contaBancariaRepository.save(conta);

        transacaoRepository.save(new Transacao(null, conta.getId(), valor, "SAQUE", OffsetDateTime.now(), null));
    }

    @Override
    public void transferir(String contaOrigem, String contaDestino, BigDecimal valor) {

    }

    @Transactional
    public void transferir(TransferenciaRequest request) {
        // Para evitar deadlocks, travar contas em ordem consistente por número
        String a = request.contaOrigem();
        String b = request.contaDestino();
        boolean same = a.equals(b);

        var first = a.compareTo(b) <= 0 ? a : b;
        var second = a.compareTo(b) <= 0 ? b : a;

        var firstAccOpt = contaBancariaRepository.findByNumeroForUpdate(first);
        var secondAccOpt = same ? firstAccOpt : contaBancariaRepository.findByNumeroForUpdate(second);

        if (firstAccOpt.isEmpty()) throw new ContaNaoEncontradaException(first);
        if (secondAccOpt.isEmpty()) throw new ContaNaoEncontradaException(second);

        var origem = a.equals(first) ? firstAccOpt.get() : secondAccOpt.get();
        var destino = a.equals(first) ? secondAccOpt.get() : firstAccOpt.get();

        if (origem.getSaldo().compareTo(request.valor()) < 0) {
            throw new SaldoInsuficienteException("Saldo insuficiente para transferência");
        }

        // debitar/creditar apenas o valor da transferência
        origem.debitar(request.valor());
        destino.creditar(request.valor());

        contaBancariaRepository.save(origem);
        if (!same) contaBancariaRepository.save(destino);

        transacaoRepository.save(new Transacao(null, origem.getId(), request.valor(), "TRANSFERENCIA_SAIDA", OffsetDateTime.now(), "para=" + destino.getNumero()));

        transacaoRepository.save(new Transacao(null, destino.getId(), request.valor(), "TRANSFERENCIA_ENTRADA", OffsetDateTime.now(), "de=" + origem.getNumero()));
    }

    @Transactional(readOnly = true)
    public Page<Transacao> extrato(Long contaBancariaId, OffsetDateTime inicio, OffsetDateTime fim, Pageable pageable) {
        if (inicio != null && fim != null) {
            return transacaoRepository.findByContaBancariaIdAndOcorridoEmBetween(contaBancariaId, inicio, fim, pageable);
        }
        return transacaoRepository.findByContaBancariaId(contaBancariaId, pageable);
    }

}
