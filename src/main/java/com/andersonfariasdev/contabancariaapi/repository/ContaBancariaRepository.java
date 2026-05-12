package com.andersonfariasdev.contabancariaapi.repository;

import com.andersonfariasdev.contabancariaapi.domain.ContaBancaria;
import jakarta.persistence.LockModeType;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Lock;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.Optional;

public interface ContaBancariaRepository extends JpaRepository<ContaBancaria, Long> {

    Optional<ContaBancaria> findByNumeroAndDigitoVerificador(String numero, String digitoVerificador);

    Optional<ContaBancaria> findByNumero(String numero);

    @Lock(LockModeType.PESSIMISTIC_WRITE)
    @Query("select c from ContaBancaria c where c.id = :id")
    Optional<ContaBancaria> findByIdForUpdate(@Param("id") Long id);

    @Lock(LockModeType.PESSIMISTIC_WRITE)
    @Query("select c from ContaBancaria c where c.numero = :numero")
    Optional<ContaBancaria> findByNumeroForUpdate(@Param("numero") String numero);

}
