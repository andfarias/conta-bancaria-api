package com.andersonfariasdev.contabancariaapi.adapters.outbound.entities;

import jakarta.persistence.*;
import lombok.*;

import java.time.OffsetDateTime;
import java.util.List;

import org.springframework.data.annotation.CreatedDate;
import org.springframework.data.annotation.LastModifiedDate;
import org.springframework.data.jpa.domain.support.AuditingEntityListener;

import com.andersonfariasdev.contabancariaapi.domain.model.enums.TipoPessoa;

@Entity
@Table(name = "clientes")
@EntityListeners(AuditingEntityListener.class)
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class ClienteJpaEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false)
    private String nomeRazao;

    @Column(nullable = false, unique = true)
    private String documento;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private TipoPessoa tipo;

    @OneToMany(mappedBy = "titular", fetch = FetchType.LAZY)
    private List<ContaBancariaJpaEntity> contas;

    @CreatedDate
    @Column(nullable = false, updatable = false)
    @Builder.Default
    private OffsetDateTime createdAt = OffsetDateTime.now();

    @LastModifiedDate
    @Column(nullable = false)
    @Builder.Default
    private OffsetDateTime updatedAt = OffsetDateTime.now();

}
