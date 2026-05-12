package com.andersonfariasdev.contabancariaapi.repository;

import com.andersonfariasdev.contabancariaapi.domain.Transacao;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;

import java.time.OffsetDateTime;

public interface TransacaoRepository extends JpaRepository<Transacao, Long> {

    Page<Transacao> findByContaBancariaIdAndOcorridoEmBetween(Long contaBancariaId, OffsetDateTime inicio, OffsetDateTime fim, Pageable pageable);

    Page<Transacao> findByContaBancariaId(Long contaBancariaId, Pageable pageable);

}
