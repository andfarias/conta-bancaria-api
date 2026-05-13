package com.andersonfariasdev.contabancariaapi.domain.repository;

import com.andersonfariasdev.contabancariaapi.domain.model.Cliente;

import java.util.List;
import java.util.Optional;

public interface ClienteRepository {
    Optional<Cliente> findById(Long id);
    Optional<Cliente> findByDocumento(String documento);
    Cliente save(Cliente cliente);
    List<Cliente> findAll();
}
