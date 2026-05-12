package com.andersonfariasdev.contabancariaapi.adapter;

import com.andersonfariasdev.contabancariaapi.domain.ContaBancaria;
import com.andersonfariasdev.contabancariaapi.port.ContaBancariaPort;
import com.andersonfariasdev.contabancariaapi.repository.ContaBancariaRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

import java.util.Optional;

@Component
@RequiredArgsConstructor
public class ContaBancariaAdapter implements ContaBancariaPort {

    private final ContaBancariaRepository repository;

    @Override
    public Optional<ContaBancaria> findByNumeroAndDigitoVerificador(String numero, String digitoVerificador) {
        return repository.findByNumeroAndDigitoVerificador(numero, digitoVerificador);
    }

    @Override
    public Optional<ContaBancaria> findByNumero(String numero) {
        return repository.findByNumero(numero);
    }

    @Override
    public Optional<ContaBancaria> findByNumeroForUpdate(String numero) {
        return repository.findByNumeroForUpdate(numero);
    }

    @Override
    public ContaBancaria save(ContaBancaria conta) {
        return repository.save(conta);
    }
}
