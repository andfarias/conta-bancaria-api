package com.andersonfariasdev.contabancariaapi.domain.repository;

import com.andersonfariasdev.contabancariaapi.domain.model.ContaBancaria;
import jakarta.persistence.LockModeType;
import org.springframework.data.jpa.repository.Lock;

import java.util.Optional;

public interface ContaBancariaRepository {
    Optional<ContaBancaria> findByNumeroAndDigitoVerificador(String numero, String digitoVerificador);

    @Lock(LockModeType.OPTIMISTIC)
    Optional<ContaBancaria> findByNumero(String numero);

    ContaBancaria save(ContaBancaria conta);

}
