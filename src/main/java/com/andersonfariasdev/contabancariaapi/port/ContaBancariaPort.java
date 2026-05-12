package com.andersonfariasdev.contabancariaapi.port;

import com.andersonfariasdev.contabancariaapi.domain.ContaBancaria;
import java.util.Optional;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import java.time.OffsetDateTime;

public interface ContaBancariaPort {

    Optional<ContaBancaria> findByNumeroAndDigitoVerificador(String numero, String digitoVerificador);

    Optional<ContaBancaria> findByNumero(String numero);

    Optional<ContaBancaria> findByNumeroForUpdate(String numero);

    ContaBancaria save(ContaBancaria conta);

}
