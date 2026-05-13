package com.andersonfariasdev.contabancariaapi.adapters.outbound.repository.jpa;

import com.andersonfariasdev.contabancariaapi.adapters.outbound.entities.CooperadoJpaEntity;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface JpaCooperadoRepository extends JpaRepository<CooperadoJpaEntity, Long> {
    Optional<CooperadoJpaEntity> findByDocumento(String documento);
}
