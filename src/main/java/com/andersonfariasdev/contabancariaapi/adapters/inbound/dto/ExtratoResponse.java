package com.andersonfariasdev.contabancariaapi.adapters.inbound.dto;

import com.andersonfariasdev.contabancariaapi.domain.model.Transacao;
import org.springframework.data.domain.Page;

import java.math.BigDecimal;

public record ExtratoResponse(Page<Transacao> transacoes, BigDecimal saldoTotal) {

}
