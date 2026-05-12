package com.andersonfariasdev.contabancariaapi.adapters.outbound.repository.jpa;

import com.andersonfariasdev.contabancariaapi.adapters.outbound.entities.ContaBancariaJpaEntity;
import jakarta.persistence.LockModeType;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Lock;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.Optional;

public interface JpaContaBancariaRepository extends JpaRepository<ContaBancariaJpaEntity, Long> {

    Optional<ContaBancariaJpaEntity> findByNumeroAndDigitoVerificador(String numero, String digitoVerificador);

    Optional<ContaBancariaJpaEntity> findByNumero(String numero);

    @Lock(LockModeType.PESSIMISTIC_WRITE)
    @Query("select c from ContaBancariaJpaEntity c where c.id = :id")
    Optional<ContaBancariaJpaEntity> findByIdForUpdate(@Param("id") Long id);

    @Lock(LockModeType.PESSIMISTIC_WRITE)
    @Query("select c from ContaBancariaJpaEntity c where c.numero = :numero")
    Optional<ContaBancariaJpaEntity> findByNumeroForUpdate(@Param("numero") String numero);

}

