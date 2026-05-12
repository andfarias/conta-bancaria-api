package com.andersonfariasdev.contabancariaapi.domain;

import jakarta.persistence.*;
import lombok.*;

import java.math.BigDecimal;

@Entity
@Table(name = "contas_bancaria")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class ContaBancaria {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false, unique = true)
    private String numero;

    @Column(nullable = false)
    private String digitoVerificador;

    @Column(nullable = false)
    private String documento; // CPF or CNPJ

    @Column(nullable = false)
    private BigDecimal saldo;

}
