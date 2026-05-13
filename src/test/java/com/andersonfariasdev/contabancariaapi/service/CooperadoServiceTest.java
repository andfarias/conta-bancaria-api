package com.andersonfariasdev.contabancariaapi.service;

import com.andersonfariasdev.contabancariaapi.adapters.inbound.dto.CooperadoRequest;
import com.andersonfariasdev.contabancariaapi.application.service.CooperadoService;
import com.andersonfariasdev.contabancariaapi.domain.model.Cooperado;
import com.andersonfariasdev.contabancariaapi.domain.model.enums.CooperadoType;
import com.andersonfariasdev.contabancariaapi.domain.model.value.Documento;
import com.andersonfariasdev.contabancariaapi.domain.repository.CooperadoRepository;
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

class CooperadoServiceTest {

    @Mock
    private CooperadoRepository cooperadoRepository;

    private CooperadoService service;

    @BeforeEach
    void setup() {
        MockitoAnnotations.openMocks(this);
        service = new CooperadoService(cooperadoRepository);
    }

    @Test
    void criarCooperadoRejeitaDocumentoDuplicado() {
        var doc = new Documento("613.443.940-19");
        var cooperado = new Cooperado(null, "Novo", doc, CooperadoType.PF);
        when(cooperadoRepository.findByDocumento("61344394019")).thenReturn(Optional.of(cooperado));

        assertThrows(ValidationException.class, () -> service.criarCooperado(cooperado));
        verify(cooperadoRepository, never()).save(any());
    }

    @Test
    void criarCooperadoPersisteQuandoDocumentoNovo() {
        var doc = new Documento("589.414.860-09");
        var cooperado = new Cooperado(null, "Novo", doc, CooperadoType.PF);
        when(cooperadoRepository.findByDocumento("58941486009")).thenReturn(Optional.empty());
        when(cooperadoRepository.save(cooperado)).thenReturn(new Cooperado(5L, "Novo", doc, CooperadoType.PF));

        var saved = service.criarCooperado(cooperado);

        assertEquals(5L, saved.getId());
        verify(cooperadoRepository).save(cooperado);
    }

    @Test
    void criarCooperadoViaDtoDelegaParaMesmaRegra() {
        var req = new CooperadoRequest("Fulano", "589.414.860-09", "PF");
        when(cooperadoRepository.findByDocumento("58941486009")).thenReturn(Optional.empty());
        when(cooperadoRepository.save(any(Cooperado.class))).thenAnswer(inv -> inv.getArgument(0));

        service.criarCooperado(req);

        verify(cooperadoRepository).save(any(Cooperado.class));
    }
}
