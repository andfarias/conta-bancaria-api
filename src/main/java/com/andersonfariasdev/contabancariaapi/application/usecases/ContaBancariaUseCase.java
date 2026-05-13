package com.andersonfariasdev.contabancariaapi.application.usecases;

import com.andersonfariasdev.contabancariaapi.adapters.inbound.dto.ExtratoResponse;
import com.andersonfariasdev.contabancariaapi.domain.model.ContaBancaria;
import com.andersonfariasdev.contabancariaapi.domain.model.Transacao;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import java.time.OffsetDateTime;

public interface ContaBancariaUseCase {
    ContaBancaria criarConta(ContaBancaria contaBancaria);
    void depositar(String numeroConta, java.math.BigDecimal valor);
    void sacar(String numeroConta, java.math.BigDecimal valor);
    void transferir(String contaOrigem, String contaDestino, java.math.BigDecimal valor);
    ExtratoResponse extrato(Long contaBancariaId, OffsetDateTime inicio, OffsetDateTime fim, Pageable pageable);
}
