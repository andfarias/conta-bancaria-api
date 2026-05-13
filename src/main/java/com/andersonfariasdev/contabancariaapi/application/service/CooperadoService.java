package com.andersonfariasdev.contabancariaapi.application.service;

import com.andersonfariasdev.contabancariaapi.adapters.inbound.dto.CooperadoRequest;
import com.andersonfariasdev.contabancariaapi.application.usecases.CooperadoUseCase;
import com.andersonfariasdev.contabancariaapi.domain.model.Cooperado;
import com.andersonfariasdev.contabancariaapi.domain.repository.CooperadoRepository;
import com.andersonfariasdev.contabancariaapi.infrastructure.exception.ValidationException;
import com.andersonfariasdev.contabancariaapi.infrastructure.mapper.CooperadoMapper;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@RequiredArgsConstructor
public class CooperadoService implements CooperadoUseCase {

    private final CooperadoRepository cooperadoRepository;

    @Override
    public Cooperado criarCooperado(Cooperado cooperado) {
        if (cooperado.getDocumento() != null
                && cooperadoRepository.findByDocumento(cooperado.getDocumento().getValor()).isPresent()) {
            throw new ValidationException("Cooperado com este documento já cadastrado");
        }
        return cooperadoRepository.save(cooperado);
    }

    @Override
    public List<Cooperado> listarCooperados() {
        return cooperadoRepository.findAll();
    }

    public Cooperado criarCooperado(CooperadoRequest cooperado) {
        return criarCooperado(CooperadoMapper.fromDtoToDomain(cooperado));
    }
}
