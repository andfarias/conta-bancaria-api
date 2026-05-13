package com.andersonfariasdev.contabancariaapi.adapters.outbound.repository;

import com.andersonfariasdev.contabancariaapi.adapters.outbound.entities.CooperadoJpaEntity;
import com.andersonfariasdev.contabancariaapi.adapters.outbound.repository.jpa.JpaCooperadoRepository;
import com.andersonfariasdev.contabancariaapi.domain.model.Cooperado;
import com.andersonfariasdev.contabancariaapi.domain.repository.CooperadoRepository;
import com.andersonfariasdev.contabancariaapi.infrastructure.mapper.CooperadoMapper;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@Component
@RequiredArgsConstructor
public class CooperadoRepositoryImpl implements CooperadoRepository {

    private final JpaCooperadoRepository repository;

    @Override
    public Optional<Cooperado> findById(Long id) {
        return repository.findById(id).map(CooperadoMapper::toDomain);
    }

    @Override
    public Optional<Cooperado> findByDocumento(String documento) {
        return repository.findByDocumento(documento).map(CooperadoMapper::toDomain);
    }

    @Override
    public Cooperado save(Cooperado cooperado) {
        var e = CooperadoMapper.toEntity(cooperado);
        var saved = repository.save(e);
        return CooperadoMapper.toDomain(saved);
    }

    @Override
    public List<Cooperado> findAll() {
        return repository.findAll().stream().map(CooperadoMapper::toDomain).collect(Collectors.toList());
    }
}
