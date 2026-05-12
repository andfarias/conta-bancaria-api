package com.andersonfariasdev.contabancariaapi.domain.repository;

import com.andersonfariasdev.contabancariaapi.domain.model.Transacao;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import java.time.OffsetDateTime;

public interface TransacaoRepository {
    Transacao save(Transacao transacao);

    Page<Transacao> findByContaBancariaId(Long contaId, Pageable pageable);

    Page<Transacao> findByContaBancariaIdAndOcorridoEmBetween(Long contaId, OffsetDateTime inicio, OffsetDateTime fim, Pageable pageable);
}
