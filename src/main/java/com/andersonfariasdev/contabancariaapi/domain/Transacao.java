package com.andersonfariasdev.contabancariaapi.domain;

import jakarta.persistence.*;
import lombok.*;

import java.math.BigDecimal;
import java.time.OffsetDateTime;

@Entity
@Table(name = "transacoes")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class Transacao {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "conta_bancaria_id", nullable = false)
    private ContaBancaria contaBancaria;

    @Column(nullable = false)
    private BigDecimal valor;

    @Column(nullable = false)
    private String tipo; // DEPOSITO, SAQUE, TRANSFERENCIA_ENTRADA, TRANSFERENCIA_SAIDA

    @Column(nullable = false)
    private OffsetDateTime ocorridoEm;

    @Column(nullable = true)
    private String metadados;

}
