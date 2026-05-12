package com.andersonfariasdev.contabancariaapi.adapters.outbound.repository;

import com.andersonfariasdev.contabancariaapi.adapters.outbound.entities.ContaBancariaJpaEntity;
import com.andersonfariasdev.contabancariaapi.adapters.outbound.entities.TransacaoJpaEntity;
import com.andersonfariasdev.contabancariaapi.adapters.outbound.repository.jpa.JpaContaBancariaRepository;
import com.andersonfariasdev.contabancariaapi.adapters.outbound.repository.jpa.JpaTransacaoRepository;
import com.andersonfariasdev.contabancariaapi.domain.model.Transacao;
import com.andersonfariasdev.contabancariaapi.domain.repository.TransacaoRepository;
import com.andersonfariasdev.contabancariaapi.infrastructure.mapper.TransacaoMapper;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Component;
import org.springframework.stereotype.Repository;

import java.time.OffsetDateTime;

@Repository
@RequiredArgsConstructor
public class TransacaoRepositoryImpl implements TransacaoRepository {

    private final JpaTransacaoRepository repository;
    private final JpaContaBancariaRepository contaRepository;

    @Override
    public Transacao save(Transacao transacao) {
        ContaBancariaJpaEntity contaE = contaRepository.findById(transacao.getContaId()).orElseThrow();
        TransacaoJpaEntity e = TransacaoMapper.toEntity(transacao, contaE);
        var saved = repository.save(e);
        return TransacaoMapper.toDomain(saved);
    }

    @Override
    public Page<Transacao> findByContaBancariaId(Long contaId, Pageable pageable) {
        return repository.findByContaBancariaId(contaId, pageable).map(TransacaoMapper::toDomain);
    }

    @Override
    public Page<Transacao> findByContaBancariaIdAndOcorridoEmBetween(Long contaId, OffsetDateTime inicio, OffsetDateTime fim, Pageable pageable) {
        return repository.findByContaBancariaIdAndOcorridoEmBetween(contaId, inicio, fim, pageable).map(TransacaoMapper::toDomain);
    }
}
