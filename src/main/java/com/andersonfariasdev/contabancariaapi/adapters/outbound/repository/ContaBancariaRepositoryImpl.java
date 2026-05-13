package com.andersonfariasdev.contabancariaapi.adapters.outbound.repository;

import com.andersonfariasdev.contabancariaapi.adapters.outbound.entities.ContaBancariaJpaEntity;
import com.andersonfariasdev.contabancariaapi.adapters.outbound.repository.jpa.JpaContaBancariaRepository;
import com.andersonfariasdev.contabancariaapi.domain.model.ContaBancaria;
import com.andersonfariasdev.contabancariaapi.domain.repository.ContaBancariaRepository;
import com.andersonfariasdev.contabancariaapi.infrastructure.mapper.ContaMapper;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
@RequiredArgsConstructor
public class ContaBancariaRepositoryImpl implements ContaBancariaRepository {

    private final JpaContaBancariaRepository repository;

    @Override
    public Optional<ContaBancaria> findByNumeroAndDigitoVerificador(String numero, String digitoVerificador) {
        return repository.findByNumeroAndDigitoVerificador(numero, digitoVerificador).map(ContaMapper::toDomain);
    }

    @Override
    public Optional<ContaBancaria> findByNumero(String numero) {
        return repository.findByNumero(numero).map(ContaMapper::toDomain);
    }

    @Override
    public ContaBancaria save(ContaBancaria conta) {
        ContaBancariaJpaEntity e = ContaMapper.toEntity(conta);
        var saved = repository.save(e);
        return ContaMapper.toDomain(saved);
    }

    @Override
    public Optional<ContaBancaria> findById(Long contaBancariaId) {
        return repository.findById(contaBancariaId)
                .flatMap(e -> Optional.ofNullable(ContaMapper.toDomain(e)));
    }
}
