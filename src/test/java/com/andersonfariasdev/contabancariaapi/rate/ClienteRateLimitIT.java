package com.andersonfariasdev.contabancariaapi.rate;

import com.andersonfariasdev.contabancariaapi.adapters.inbound.dto.ClienteRequest;
import com.andersonfariasdev.contabancariaapi.application.service.ClienteService;
import com.andersonfariasdev.contabancariaapi.infrastructure.exception.ValidationException;
import io.github.resilience4j.ratelimiter.RequestNotPermitted;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

import static org.junit.jupiter.api.Assertions.assertThrows;

@SpringBootTest
public class ClienteRateLimitIT {

    @Autowired
    ClienteService clienteService;

    @Test
    void quandoExcedeRateLimit_deveLancarRequestNotPermitted() {
        var req = new ClienteRequest("Teste", "12345678901", "PF");

        // 5 chamadas permitidas (configuração em application.properties)
        for (int i = 0; i < 5; i++) {
            try {
                clienteService.criarCliente(req);
            } catch (ValidationException e) {
                // ignore domain validation in case of duplicate document in DB
            }
        }

        // a 6ª chamada deve exceder o RateLimiter
        assertThrows(RequestNotPermitted.class, () -> clienteService.criarCliente(req));
    }
}
