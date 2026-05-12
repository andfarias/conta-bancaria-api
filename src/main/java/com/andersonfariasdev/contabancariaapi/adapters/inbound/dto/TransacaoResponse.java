package com.andersonfariasdev.contabancariaapi.adapters.inbound.dto;

import java.math.BigDecimal;
import java.time.OffsetDateTime;

public record TransacaoResponse(BigDecimal valor, String tipo, OffsetDateTime ocorridoEm, String metadados) {}
