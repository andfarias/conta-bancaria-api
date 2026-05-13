package com.andersonfariasdev.contabancariaapi.application.usecases;

import com.andersonfariasdev.contabancariaapi.domain.model.Cooperado;

import java.util.List;

public interface CooperadoUseCase {
    Cooperado criarCooperado(Cooperado cooperado);
    List<Cooperado> listarCooperados();
}
