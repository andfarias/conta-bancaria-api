package com.andersonfariasdev.contabancariaapi.adapters.inbound.controller;

import com.andersonfariasdev.contabancariaapi.adapters.inbound.dto.ClienteRequest;
import com.andersonfariasdev.contabancariaapi.application.service.ClienteService;
import com.andersonfariasdev.contabancariaapi.domain.model.Cliente;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/clientes")
@RequiredArgsConstructor
public class ClienteController {

    private final ClienteService clienteService;

    @PostMapping
    public ResponseEntity<Cliente> criar(@RequestBody @Valid ClienteRequest cliente) {
        var created = clienteService.criarCliente(cliente);
        return ResponseEntity.ok(created);
    }

    @GetMapping
    public ResponseEntity<List<Cliente>> listar() {
        return ResponseEntity.ok(clienteService.listarClientes());
    }
}
