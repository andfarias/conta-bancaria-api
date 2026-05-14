package com.andersonfariasdev.contabancariaapi.adapters.outbound.entities;

import com.andersonfariasdev.contabancariaapi.domain.model.enums.StatusConta;
import com.andersonfariasdev.contabancariaapi.domain.model.enums.TipoConta;
import jakarta.persistence.*;
import lombok.*;
import org.hibernate.envers.Audited;
import org.springframework.data.annotation.CreatedDate;
import org.springframework.data.annotation.LastModifiedDate;
import org.springframework.data.jpa.domain.support.AuditingEntityListener;

import java.math.BigDecimal;
import java.time.OffsetDateTime;

@Entity
@Table(name = "contas_bancaria", uniqueConstraints = {
        @UniqueConstraint(columnNames = {"numero", "digitoVerificador"})
})
@Audited
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

    @Column(nullable = false)
    @Builder.Default
    private String agencia = "0001"; // Agência default

    @Column(nullable = false)
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
    private OffsetDateTime createdAt = OffsetDateTime.now();

    @LastModifiedDate
    @Column(nullable = false)
    private OffsetDateTime updatedAt = OffsetDateTime.now();

}
