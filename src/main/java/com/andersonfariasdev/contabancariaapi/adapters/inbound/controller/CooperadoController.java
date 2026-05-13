package com.andersonfariasdev.contabancariaapi.adapters.inbound.controller;

import com.andersonfariasdev.contabancariaapi.adapters.inbound.dto.CooperadoRequest;
import com.andersonfariasdev.contabancariaapi.application.service.CooperadoService;
import com.andersonfariasdev.contabancariaapi.domain.model.Cooperado;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/cooperados")
@RequiredArgsConstructor
public class CooperadoController {

    private final CooperadoService cooperadoService;

    @PostMapping
    public ResponseEntity<Cooperado> criar(@RequestBody @Valid CooperadoRequest cooperado) {
        var created = cooperadoService.criarCooperado(cooperado);
        return ResponseEntity.ok(created);
    }

    @GetMapping
    public ResponseEntity<List<Cooperado>> listar() {
        return ResponseEntity.ok(cooperadoService.listarCooperados());
    }
}
