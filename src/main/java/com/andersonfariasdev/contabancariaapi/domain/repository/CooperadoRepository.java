package com.andersonfariasdev.contabancariaapi.domain.repository;

import com.andersonfariasdev.contabancariaapi.domain.model.Cooperado;

import java.util.List;
import java.util.Optional;

public interface CooperadoRepository {
    Optional<Cooperado> findById(Long id);
    Optional<Cooperado> findByDocumento(String documento);
    Cooperado save(Cooperado cooperado);
    List<Cooperado> findAll();
}
