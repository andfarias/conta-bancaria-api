package com.andersonfariasdev.contabancariaapi.controller;

import com.andersonfariasdev.contabancariaapi.adapters.inbound.controller.ContaBancariaController;
import com.andersonfariasdev.contabancariaapi.adapters.inbound.dto.ValorTransacaoRequest;
import com.andersonfariasdev.contabancariaapi.application.service.ContaBancariaService;
import com.andersonfariasdev.contabancariaapi.infrastructure.exception.SaldoInsuficienteException;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.webmvc.test.autoconfigure.WebMvcTest;
import org.springframework.http.MediaType;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.servlet.MockMvc;

import java.math.BigDecimal;

import static org.mockito.Mockito.doThrow;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@WebMvcTest(controllers = ContaBancariaController.class)
class ContaBancariaControllerTest {

    @Autowired
    MockMvc mvc;

    @MockitoBean
    ContaBancariaService service;

    @Test
    void saqueSaldoInsuficienteRetorna422() throws Exception {
        var req = new ValorTransacaoRequest("999", new BigDecimal("1000.00"));

        doThrow(new SaldoInsuficienteException("Saldo insuficiente")).when(service).sacar(req.numeroConta(), req.valor());

        mvc.perform(post("/api/contas/saque")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("{\"numeroConta\":\"999\",\"valor\":1000.00}"))
                .andExpect(status().isUnprocessableEntity());
    }
}
