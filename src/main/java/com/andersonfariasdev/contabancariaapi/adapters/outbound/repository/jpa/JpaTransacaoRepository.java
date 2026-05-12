package com.andersonfariasdev.contabancariaapi.adapters.outbound.repository.jpa;

import com.andersonfariasdev.contabancariaapi.adapters.outbound.entities.TransacaoJpaEntity;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;

import java.time.OffsetDateTime;

public interface JpaTransacaoRepository extends JpaRepository<TransacaoJpaEntity, Long> {

    Page<TransacaoJpaEntity> findByContaBancariaIdAndOcorridoEmBetween(Long contaBancariaId, OffsetDateTime inicio, OffsetDateTime fim, Pageable pageable);

    Page<TransacaoJpaEntity> findByContaBancariaId(Long contaBancariaId, Pageable pageable);

}