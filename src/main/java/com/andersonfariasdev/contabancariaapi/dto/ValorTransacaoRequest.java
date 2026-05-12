package com.andersonfariasdev.contabancariaapi.dto;

import jakarta.validation.constraints.DecimalMin;
import jakarta.validation.constraints.NotBlank;
import lombok.*;

import java.math.BigDecimal;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class ValorTransacaoRequest {

    @NotBlank
    private String numeroConta;

    @DecimalMin(value = "0.01")
    private BigDecimal valor;

}
