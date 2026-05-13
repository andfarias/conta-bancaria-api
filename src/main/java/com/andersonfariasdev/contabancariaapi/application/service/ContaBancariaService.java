package com.andersonfariasdev.contabancariaapi.application.service;

import com.andersonfariasdev.contabancariaapi.adapters.inbound.dto.ContaBancariaCriacaoRequest;
import com.andersonfariasdev.contabancariaapi.adapters.inbound.dto.TransferenciaRequest;
import com.andersonfariasdev.contabancariaapi.application.usecases.ContaBancariaUseCase;
import com.andersonfariasdev.contabancariaapi.domain.model.ContaBancaria;
import com.andersonfariasdev.contabancariaapi.domain.model.Transacao;
import com.andersonfariasdev.contabancariaapi.domain.model.enums.StatusConta;
import com.andersonfariasdev.contabancariaapi.domain.model.enums.StatusTransacao;
import com.andersonfariasdev.contabancariaapi.domain.model.enums.TipoConta;
import com.andersonfariasdev.contabancariaapi.domain.model.enums.TipoTransacao;
import com.andersonfariasdev.contabancariaapi.domain.model.value.NumeroConta;
import com.andersonfariasdev.contabancariaapi.domain.repository.ContaBancariaRepository;
import com.andersonfariasdev.contabancariaapi.domain.repository.TransacaoRepository;
import com.andersonfariasdev.contabancariaapi.infrastructure.exception.ContaNaoEncontradaException;
import com.andersonfariasdev.contabancariaapi.infrastructure.exception.SaldoInsuficienteException;
import com.andersonfariasdev.contabancariaapi.infrastructure.exception.ValidationException;
import lombok.RequiredArgsConstructor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;


import java.math.BigDecimal;
import java.time.OffsetDateTime;
import java.util.Arrays;

@Service
@RequiredArgsConstructor
public class ContaBancariaService implements ContaBancariaUseCase {

    private static final Logger log = LoggerFactory.getLogger(ContaBancariaService.class);

    private final ContaBancariaRepository contaBancariaRepository;
    private final TransacaoRepository transacaoRepository;
    private final com.andersonfariasdev.contabancariaapi.domain.repository.CooperadoRepository cooperadoRepository;

    @Transactional
    public ContaBancaria criarConta(ContaBancaria contaBancaria) {
        // validações simples para número, dígito e documento
        if (contaBancaria.getNumero() == null || contaBancaria.getDigitoVerificador() == null) {
            throw new ValidationException("numero da conta e digito verificador são obrigatórios");
        }
        return contaBancariaRepository.save(contaBancaria);
    }

    // new helper: create account by cooperado id and request
    @Transactional
    public ContaBancaria criarConta(Long cooperadoId, ContaBancariaCriacaoRequest req) {
        var coopOpt = cooperadoRepository.findById(cooperadoId);
        var coop = coopOpt.orElseThrow(() -> new ValidationException("Cooperado não encontrado"));

        var tipoConta = Arrays.stream(TipoConta.values())
                .filter(t -> t.name().equalsIgnoreCase(req.tipoConta()))
                .findFirst()
                .orElseThrow(() -> new ValidationException("Tipo de conta deve ser CORRENTE ou POUPANCA"));

        if (contaBancariaRepository.findByNumero(req.numero()).isPresent()) {
            throw new ValidationException("Já existe conta com este número");
        }

        var acc = new ContaBancaria(null, new NumeroConta(req.numero()), req.digitoVerificador(), coop, tipoConta, StatusConta.ATIVA, null);
        return criarConta(acc);
    }

    @Transactional
    public void depositar(String numeroConta, BigDecimal valor) {
        var opt = contaBancariaRepository.findByNumero(numeroConta);
        if (opt.isEmpty()) throw new ContaNaoEncontradaException(numeroConta);
        var conta = opt.get();
        // creditar apenas o valor da transação
        conta.creditar(valor);
        contaBancariaRepository.save(conta);

        transacaoRepository.save(new Transacao(null, conta.getId(), valor, TipoTransacao.DEPOSITO, StatusTransacao.CONCLUIDA, OffsetDateTime.now()));
    }

    @Transactional
    public void sacar(String numeroConta, BigDecimal valor) {
        var opt = contaBancariaRepository.findByNumero(numeroConta);
        if (opt.isEmpty()) throw new ContaNaoEncontradaException(numeroConta);
        var conta = opt.get();

        if (conta.getSaldo().compareTo(valor) < 0) {
            throw new SaldoInsuficienteException("Saldo insuficiente");
        }

        conta.debitar(valor);
        contaBancariaRepository.save(conta);

        transacaoRepository.save(new Transacao(null, conta.getId(), valor, TipoTransacao.SAQUE, StatusTransacao.CONCLUIDA, OffsetDateTime.now()));
    }

    @Override
    public void transferir(String contaOrigem, String contaDestino, BigDecimal valor) {

    }

    @Transactional
    public void transferir(TransferenciaRequest request) {
        log.info("Iniciando transferência: {} -> {} valor={}", request.contaOrigem(), request.contaDestino(), request.valor());
        // Para evitar deadlocks, travar contas em ordem consistente por número
        String a = request.contaOrigem();
        String b = request.contaDestino();
        boolean same = a.equals(b);

        var first = a.compareTo(b) <= 0 ? a : b;
        var second = a.compareTo(b) <= 0 ? b : a;

        var firstAccOpt = contaBancariaRepository.findByNumero(first);
        var secondAccOpt = same ? firstAccOpt : contaBancariaRepository.findByNumero(second);

        if (firstAccOpt.isEmpty()) throw new ContaNaoEncontradaException(first);
        if (secondAccOpt.isEmpty()) throw new ContaNaoEncontradaException(second);

        var origem = a.equals(first) ? firstAccOpt.get() : secondAccOpt.get();
        var destino = a.equals(first) ? secondAccOpt.get() : firstAccOpt.get();

        log.debug("Saldos antes: origem={} destino={}", origem.getSaldo(), destino.getSaldo());

        if (origem.getSaldo().compareTo(request.valor()) < 0) {
            throw new SaldoInsuficienteException("Saldo insuficiente para transferência");
        }

        // debitar/creditar apenas o valor da transferência
        origem.debitar(request.valor());
        destino.creditar(request.valor());

        var savedOrigem = contaBancariaRepository.save(origem);
        if (!same) {
            var savedDestino = contaBancariaRepository.save(destino);
            log.debug("Saldos salvos: origem={} destino={}", savedOrigem.getSaldo(), savedDestino.getSaldo());
        } else {
            log.debug("Saldo salvo (mesma conta): {}", savedOrigem.getSaldo());
        }

        transacaoRepository.save(new Transacao(null, origem.getId(), request.valor(), TipoTransacao.TRANSFERENCIA_SAIDA, StatusTransacao.CONCLUIDA, OffsetDateTime.now(), "para=" + destino.getNumero()));

        transacaoRepository.save(new Transacao(null, destino.getId(), request.valor(), TipoTransacao.TRANSFERENCIA_ENTRADA, StatusTransacao.CONCLUIDA, OffsetDateTime.now(), "de=" + origem.getNumero()));
    }

    @Transactional(readOnly = true)
    public Page<Transacao> extrato(Long contaBancariaId, OffsetDateTime inicio, OffsetDateTime fim, Pageable pageable) {
        if (inicio != null && fim != null) {
            return transacaoRepository.findByContaBancariaIdAndOcorridoEmBetween(contaBancariaId, inicio, fim, pageable);
        }
        return transacaoRepository.findByContaBancariaId(contaBancariaId, pageable);
    }

}
