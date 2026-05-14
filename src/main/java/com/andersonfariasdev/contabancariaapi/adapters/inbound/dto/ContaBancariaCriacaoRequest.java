package com.andersonfariasdev.contabancariaapi.adapters.inbound.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Pattern;

public record ContaBancariaCriacaoRequest(
        @NotBlank @Pattern(regexp = "^\\d{6}$", message="Número da conta deve conter 6 caracteres") String numero,
        @NotBlank @Pattern(regexp = "^[0-9Xx]{1}$", message="Dígito verificador inválido.") String digitoVerificador,
        @NotBlank String tipoConta,
        Long clienteId
) {
}
