package com.andersonfariasdev.contabancariaapi.dto;

import lombok.*;

import java.math.BigDecimal;
import java.time.OffsetDateTime;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class TransacaoResponse {

    private Long id;
    private BigDecimal valor;
    private String tipo;
    private OffsetDateTime ocorridoEm;
    private String metadados;

}
