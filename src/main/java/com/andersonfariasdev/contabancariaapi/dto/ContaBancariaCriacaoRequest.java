package com.andersonfariasdev.contabancariaapi.dto;

import jakarta.validation.constraints.NotBlank;
import lombok.*;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class ContaBancariaCriacaoRequest {

    @NotBlank
    private String numero;

    @NotBlank
    private String digitoVerificador;

    @NotBlank
    private String documento;

}
