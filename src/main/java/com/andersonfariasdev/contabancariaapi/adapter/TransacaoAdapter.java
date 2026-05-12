package com.andersonfariasdev.contabancariaapi.adapter;

import com.andersonfariasdev.contabancariaapi.domain.Transacao;
import com.andersonfariasdev.contabancariaapi.port.TransacaoPort;
import com.andersonfariasdev.contabancariaapi.repository.TransacaoRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Component;

import java.util.Optional;

@Component
@RequiredArgsConstructor
public class TransacaoAdapter implements TransacaoPort {

    private final TransacaoRepository repository;

    @Override
    public Transacao save(Transacao transacao) {
        return repository.save(transacao);
    }

    @Override
    public Page<Transacao> findByContaBancariaId(Long contaId, Pageable pageable) {
        return repository.findByContaBancariaId(contaId, pageable);
    }

    @Override
    public Page<Transacao> findByContaBancariaIdAndOcorridoEmBetween(Long contaId, java.time.OffsetDateTime inicio, java.time.OffsetDateTime fim, Pageable pageable) {
        return repository.findByContaBancariaIdAndOcorridoEmBetween(contaId, inicio, fim, pageable);
    }
}
