package com.andersonfariasdev.contabancariaapi.adapters.inbound.controller;

import com.andersonfariasdev.contabancariaapi.adapters.inbound.dto.ClienteRequest;
import com.andersonfariasdev.contabancariaapi.application.service.ClienteService;
import com.andersonfariasdev.contabancariaapi.domain.model.Cliente;
import com.andersonfariasdev.contabancariaapi.domain.model.enums.TipoPessoa;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
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

    @GetMapping("/search")
    public ResponseEntity<Page<Cliente>> search(
            @RequestParam(required = false) String nomeRazao,
            @RequestParam(required = false) String documento,
            @RequestParam(required = false) String tipo,
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "20") int size
    ) {
        Pageable p = PageRequest.of(page, Math.min(size, 1000));
        TipoPessoa tipoEnum = null;
        if (tipo != null && !tipo.isBlank()) {
            tipoEnum = java.util.Arrays.stream(TipoPessoa.values())
                    .filter(t -> t.name().equalsIgnoreCase(tipo))
                    .findFirst().orElse(null);
        }
        var res = clienteService.search(nomeRazao, documento, tipoEnum, p);
        return ResponseEntity.ok(res);
    }
}
