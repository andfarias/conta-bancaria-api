package com.andersonfariasdev.contabancariaapi.adapters.outbound.repository.jpa;

import com.andersonfariasdev.contabancariaapi.adapters.outbound.entities.ContaBancariaJpaEntity;
import jakarta.persistence.LockModeType;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Lock;

import java.util.Optional;

public interface JpaContaBancariaRepository extends JpaRepository<ContaBancariaJpaEntity, Long> {

    Optional<ContaBancariaJpaEntity> findByNumeroAndDigitoVerificador(String numero, String digitoVerificador);

    @Lock(LockModeType.OPTIMISTIC)
    Optional<ContaBancariaJpaEntity> findByNumero(String numero);

}
