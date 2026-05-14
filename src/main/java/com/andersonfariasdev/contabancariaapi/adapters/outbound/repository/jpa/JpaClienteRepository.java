package com.andersonfariasdev.contabancariaapi.adapters.outbound.repository.jpa;

import com.andersonfariasdev.contabancariaapi.adapters.outbound.entities.ClienteJpaEntity;
import com.andersonfariasdev.contabancariaapi.domain.model.enums.TipoPessoa;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.Optional;

public interface JpaClienteRepository extends JpaRepository<ClienteJpaEntity, Long> {
    Optional<ClienteJpaEntity> findByDocumento(String documento);

    @Query("select c from ClienteJpaEntity c where " +
            "(cast(:nomeRazao as string) is null or lower(c.nomeRazao) like lower(concat('%', cast(:nomeRazao as string), '%'))) and " +
            "(:documento is null or c.documento = :documento) and " +
            "(:tipo is null or c.tipo = :tipo)")
    Page<ClienteJpaEntity> search(@Param("nomeRazao") String nomeRazao,
                                  @Param("documento") String documento,
                                  @Param("tipo") TipoPessoa tipo,
                                  Pageable pageable);
}
