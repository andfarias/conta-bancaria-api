package com.andersonfariasdev.contabancariaapi.controller;

import com.andersonfariasdev.contabancariaapi.domain.ContaBancaria;
import com.andersonfariasdev.contabancariaapi.domain.Transacao;
import com.andersonfariasdev.contabancariaapi.dto.ContaBancariaCriacaoRequest;
import com.andersonfariasdev.contabancariaapi.dto.ValorTransacaoRequest;
import com.andersonfariasdev.contabancariaapi.dto.TransferenciaRequest;
import com.andersonfariasdev.contabancariaapi.service.ContaBancariaService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import java.time.OffsetDateTime;
import java.time.format.DateTimeParseException;

@RestController
@RequestMapping("/api/contas")
@RequiredArgsConstructor
@Validated
public class ContaBancariaController {

    private final ContaBancariaService contaBancariaService;

    @PostMapping
    public ResponseEntity<ContaBancaria> criar(@RequestBody @Valid ContaBancariaCriacaoRequest req) {
        var acc = ContaBancaria.builder()
                .numero(req.getNumero())
                .digitoVerificador(req.getDigitoVerificador())
                .documento(req.getDocumento())
                .build();
        var created = contaBancariaService.criarConta(acc);
        return ResponseEntity.ok(created);
    }

    @PostMapping("/deposito")
    public ResponseEntity<?> depositar(@RequestBody @Valid ValorTransacaoRequest req) {
        contaBancariaService.depositar(req);
        return ResponseEntity.ok().build();
    }

    @PostMapping("/saque")
    public ResponseEntity<?> sacar(@RequestBody @Valid ValorTransacaoRequest req) {
        contaBancariaService.sacar(req);
        return ResponseEntity.ok().build();
    }

    @PostMapping("/transferencia")
    public ResponseEntity<?> transferir(@RequestBody @Valid TransferenciaRequest req) {
        contaBancariaService.transferir(req);
        return ResponseEntity.ok().build();
    }

    @GetMapping("/{contaId}/extrato")
    public ResponseEntity<Page<Transacao>> extrato(@PathVariable("contaId") Long contaId,
                                                   @RequestParam(required = false) String inicio,
                                                   @RequestParam(required = false) String fim,
                                                   @RequestParam(defaultValue = "0") int page,
                                                   @RequestParam(defaultValue = "20") int size) {
        OffsetDateTime s = null, e = null;
        try {
            if (inicio != null) s = OffsetDateTime.parse(inicio);
            if (fim != null) e = OffsetDateTime.parse(fim);
        } catch (DateTimeParseException ex) {
            return ResponseEntity.badRequest().build();
        }
        Pageable p = PageRequest.of(page, Math.min(size, 1000));
        var res = contaBancariaService.extrato(contaId, s, e, p);
        return ResponseEntity.ok(res);
    }

}
