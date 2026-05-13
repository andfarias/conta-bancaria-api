package com.andersonfariasdev.contabancariaapi.adapters.inbound.dto;

import jakarta.validation.constraints.NotBlank;

public record ContaBancariaCriacaoRequest(
        @NotBlank String numero,
        @NotBlank String digitoVerificador,
        @NotBlank String tipoConta,
        Long cooperadoId
) {}
