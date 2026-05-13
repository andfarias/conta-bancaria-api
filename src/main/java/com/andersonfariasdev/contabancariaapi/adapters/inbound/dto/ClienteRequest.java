package com.andersonfariasdev.contabancariaapi.adapters.inbound.dto;

import jakarta.validation.constraints.NotBlank;

public record ClienteRequest(
        @NotBlank String nomeRazao,
        @NotBlank String documento,
        @NotBlank String tipo) {
}