package com.andersonfariasdev.contabancariaapi.application.usecases;

import com.andersonfariasdev.contabancariaapi.domain.model.Cliente;

import java.util.List;

public interface ClienteUseCase {
    Cliente criarCliente(Cliente cliente);
    List<Cliente> listarClientes();
}
