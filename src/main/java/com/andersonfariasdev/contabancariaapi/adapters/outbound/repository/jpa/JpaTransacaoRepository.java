package com.andersonfariasdev.contabancariaapi.adapters.outbound.repository.jpa;

import com.andersonfariasdev.contabancariaapi.adapters.outbound.entities.TransacaoJpaEntity;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;

import java.time.OffsetDateTime;

public interface JpaTransacaoRepository extends JpaRepository<TransacaoJpaEntity, Long> {

    Page<TransacaoJpaEntity> findByConta_IdAndOcorridoEmBetween(Long contaId, OffsetDateTime inicio, OffsetDateTime fim, Pageable pageable);

    Page<TransacaoJpaEntity> findByConta_Id(Long contaId, Pageable pageable);

}