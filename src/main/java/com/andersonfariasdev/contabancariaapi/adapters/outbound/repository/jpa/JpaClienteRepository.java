package com.andersonfariasdev.contabancariaapi.adapters.outbound.repository.jpa;

import com.andersonfariasdev.contabancariaapi.adapters.outbound.entities.ClienteJpaEntity;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface JpaClienteRepository extends JpaRepository<ClienteJpaEntity, Long> {
    Optional<ClienteJpaEntity> findByDocumento(String documento);
}
