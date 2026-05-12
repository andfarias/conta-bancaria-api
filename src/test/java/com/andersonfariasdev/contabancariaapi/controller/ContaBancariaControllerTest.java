package com.andersonfariasdev.contabancariaapi.controller;

import com.andersonfariasdev.contabancariaapi.dto.ValorTransacaoRequest;
import com.andersonfariasdev.contabancariaapi.service.ContaBancariaService;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.webmvc.test.autoconfigure.WebMvcTest;
import org.springframework.http.MediaType;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.servlet.MockMvc;

import java.math.BigDecimal;

import static org.mockito.ArgumentMatchers.any;
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
        var req = new ValorTransacaoRequest();
        req.setNumeroConta("999");
        req.setValor(new BigDecimal("1000.00"));

        doThrow(new com.andersonfariasdev.contabancariaapi.exception.SaldoInsuficienteException("Saldo insuficiente")).when(service).sacar(any());

        mvc.perform(post("/api/contas/saque")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("{\"numeroConta\":\"999\",\"valor\":1000.00}"))
                .andExpect(status().isUnprocessableEntity());
    }
}
