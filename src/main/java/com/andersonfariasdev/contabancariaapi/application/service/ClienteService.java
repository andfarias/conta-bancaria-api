package com.andersonfariasdev.contabancariaapi.application.service;

import com.andersonfariasdev.contabancariaapi.adapters.inbound.dto.ClienteRequest;
import com.andersonfariasdev.contabancariaapi.application.usecases.ClienteUseCase;
import com.andersonfariasdev.contabancariaapi.domain.model.Cliente;
import com.andersonfariasdev.contabancariaapi.domain.repository.ClienteRepository;
import com.andersonfariasdev.contabancariaapi.infrastructure.exception.ValidationException;
import com.andersonfariasdev.contabancariaapi.infrastructure.mapper.ClienteMapper;
import io.github.resilience4j.ratelimiter.annotation.RateLimiter;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@RequiredArgsConstructor
public class ClienteService implements ClienteUseCase {

    private final ClienteRepository clienteRepository;

    @Override
    public Cliente criarCliente(Cliente cliente) {
        if (cliente.getDocumento() != null
                && clienteRepository.findByDocumento(cliente.getDocumento().getValor()).isPresent()) {
            throw new ValidationException("Cliente com este documento já cadastrado");
        }
        return clienteRepository.save(cliente);
    }

    @Override
    public List<Cliente> listarClientes() {
        return clienteRepository.findAll();
    }

    public Page<Cliente> search(String nomeRazao, String documento, com.andersonfariasdev.contabancariaapi.domain.model.enums.TipoPessoa tipo, org.springframework.data.domain.Pageable pageable) {
        return clienteRepository.search(nomeRazao, documento, tipo, pageable);
    }

    @RateLimiter(name = "criacaoCliente")
    public Cliente criarCliente(ClienteRequest cliente) {
        return criarCliente(ClienteMapper.fromDtoToDomain(cliente));
    }
}
