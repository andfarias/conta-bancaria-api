package com.andersonfariasdev.contabancariaapi.adapters.outbound.repository;

import com.andersonfariasdev.contabancariaapi.adapters.outbound.repository.jpa.JpaClienteRepository;
import com.andersonfariasdev.contabancariaapi.domain.model.Cliente;
import com.andersonfariasdev.contabancariaapi.domain.repository.ClienteRepository;
import com.andersonfariasdev.contabancariaapi.infrastructure.mapper.ClienteMapper;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@Component
@RequiredArgsConstructor
public class ClienteRepositoryImpl implements ClienteRepository {

    private final JpaClienteRepository repository;

    @Override
    public Optional<Cliente> findById(Long id) {
        return repository.findById(id).map(ClienteMapper::toDomain);
    }

    @Override
    public Optional<Cliente> findByDocumento(String documento) {
        return repository.findByDocumento(documento).map(ClienteMapper::toDomain);
    }

    @Override
    public Cliente save(Cliente cliente) {
        var e = ClienteMapper.toEntity(cliente);
        var saved = repository.save(e);
        return ClienteMapper.toDomain(saved);
    }

    @Override
    public List<Cliente> findAll() {
        return repository.findAll().stream().map(ClienteMapper::toDomain).collect(Collectors.toList());
    }

    @Override
    public Page<Cliente> search(String nomeRazao, String documento, com.andersonfariasdev.contabancariaapi.domain.model.enums.TipoPessoa tipo, org.springframework.data.domain.Pageable pageable) {
        var page = repository.search(nomeRazao, documento, tipo, pageable);
        return page.map(ClienteMapper::toDomain);
    }
}
