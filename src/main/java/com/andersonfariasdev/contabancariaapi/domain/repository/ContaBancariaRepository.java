package com.andersonfariasdev.contabancariaapi.domain.repository;

import com.andersonfariasdev.contabancariaapi.domain.model.ContaBancaria;

import java.util.Optional;

public interface ContaBancariaRepository {
    Optional<ContaBancaria> findByNumeroAndDigitoVerificador(String numero, String digitoVerificador);

    Optional<ContaBancaria> findByNumero(String numero);

    Optional<ContaBancaria> findByNumeroForUpdate(String numero);

    ContaBancaria save(ContaBancaria conta);

}
