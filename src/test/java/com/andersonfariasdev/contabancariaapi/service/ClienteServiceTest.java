package com.andersonfariasdev.contabancariaapi.service;

import com.andersonfariasdev.contabancariaapi.adapters.inbound.dto.ClienteRequest;
import com.andersonfariasdev.contabancariaapi.application.service.ClienteService;
import com.andersonfariasdev.contabancariaapi.domain.model.Cliente;
import com.andersonfariasdev.contabancariaapi.domain.model.enums.TipoPessoa;
import com.andersonfariasdev.contabancariaapi.domain.model.value.Documento;
import com.andersonfariasdev.contabancariaapi.domain.repository.ClienteRepository;
import com.andersonfariasdev.contabancariaapi.infrastructure.exception.ValidationException;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;

import java.util.Optional;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

class ClienteServiceTest {

    @Mock
    private ClienteRepository clienteRepository;

    private ClienteService service;

    @BeforeEach
    void setup() {
        MockitoAnnotations.openMocks(this);
        service = new ClienteService(clienteRepository);
    }

    @Test
    void criarClienteRejeitaDocumentoDuplicado() {
        var doc = new Documento("613.443.940-19");
        var cliente = new Cliente(null, "Novo", doc, TipoPessoa.PF);
        when(clienteRepository.findByDocumento("61344394019")).thenReturn(Optional.of(cliente));

        assertThrows(ValidationException.class, () -> service.criarCliente(cliente));
        verify(clienteRepository, never()).save(any());
    }

    @Test
    void criarClientePersisteQuandoDocumentoNovo() {
        var doc = new Documento("589.414.860-09");
        var cliente = new Cliente(null, "Novo", doc, TipoPessoa.PF);
        when(clienteRepository.findByDocumento("58941486009")).thenReturn(Optional.empty());
        when(clienteRepository.save(cliente)).thenReturn(new Cliente(5L, "Novo", doc, TipoPessoa.PF));

        var saved = service.criarCliente(cliente);

        assertEquals(5L, saved.getId());
        verify(clienteRepository).save(cliente);
    }

    @Test
    void criarClienteViaDtoDelegaParaMesmaRegra() {
        var req = new ClienteRequest("Fulano", "589.414.860-09", "PF");
        when(clienteRepository.findByDocumento("58941486009")).thenReturn(Optional.empty());
        when(clienteRepository.save(any(Cliente.class))).thenAnswer(inv -> inv.getArgument(0));

        service.criarCliente(req);

        verify(clienteRepository).save(any(Cliente.class));
    }
}
