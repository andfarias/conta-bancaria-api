package com.andersonfariasdev.contabancariaapi.adapters.inbound.dto;

import jakarta.validation.constraints.DecimalMin;
import jakarta.validation.constraints.NotBlank;
import java.math.BigDecimal;

public record ValorTransacaoRequest(
        @NotBlank String numeroConta,
        @DecimalMin("0.01") BigDecimal valor
) {}
