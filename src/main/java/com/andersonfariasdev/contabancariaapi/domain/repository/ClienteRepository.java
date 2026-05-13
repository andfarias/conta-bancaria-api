package com.andersonfariasdev.contabancariaapi.domain.repository;

import com.andersonfariasdev.contabancariaapi.domain.model.Cliente;
import org.springframework.data.domain.Page;

import java.util.List;
import java.util.Optional;

public interface ClienteRepository {
    Optional<Cliente> findById(Long id);

    Optional<Cliente> findByDocumento(String documento);

    Cliente save(Cliente cliente);

    List<Cliente> findAll();

    Page<Cliente> search(String nomeRazao, String documento, com.andersonfariasdev.contabancariaapi.domain.model.enums.TipoPessoa tipo, org.springframework.data.domain.Pageable pageable);
}
