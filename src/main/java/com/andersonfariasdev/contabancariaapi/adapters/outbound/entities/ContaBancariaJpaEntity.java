package com.andersonfariasdev.contabancariaapi.adapters.outbound.entities;

import jakarta.persistence.*;
import lombok.*;

import java.math.BigDecimal;
import java.time.OffsetDateTime;

import org.springframework.data.annotation.CreatedDate;
import org.springframework.data.annotation.LastModifiedDate;
import org.springframework.data.jpa.domain.support.AuditingEntityListener;

import com.andersonfariasdev.contabancariaapi.domain.model.enums.StatusConta;
import com.andersonfariasdev.contabancariaapi.domain.model.enums.TipoConta;

@Entity
@Table(name = "contas_bancaria")
@EntityListeners(AuditingEntityListener.class)
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class ContaBancariaJpaEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false, unique = true)
    private String numero;

    @Column(nullable = false)
    private String digitoVerificador;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "titular_id", nullable = true)
    private ClienteJpaEntity titular;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private TipoConta tipo = TipoConta.CORRENTE;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private StatusConta status = StatusConta.ATIVA;

    @Column(nullable = false)
    private BigDecimal saldo;

    @Version
    @Column(nullable = false)
    private Long version = 0L;

    @CreatedDate
    @Column(nullable = false, updatable = false)
    private OffsetDateTime createdAt  = OffsetDateTime.now();

    @LastModifiedDate
    @Column(nullable = false)
    private OffsetDateTime updatedAt = OffsetDateTime.now();

}
