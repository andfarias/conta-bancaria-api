package com.andersonfariasdev.contabancariaapi.integration;

import com.andersonfariasdev.contabancariaapi.adapters.inbound.dto.TransferenciaRequest;
import com.andersonfariasdev.contabancariaapi.adapters.outbound.entities.ContaBancariaJpaEntity;
import com.andersonfariasdev.contabancariaapi.adapters.outbound.entities.ClienteJpaEntity;
import com.andersonfariasdev.contabancariaapi.adapters.outbound.repository.jpa.JpaContaBancariaRepository;
import com.andersonfariasdev.contabancariaapi.adapters.outbound.repository.jpa.JpaClienteRepository;
import com.andersonfariasdev.contabancariaapi.application.service.ContaBancariaService;
import com.andersonfariasdev.contabancariaapi.domain.model.Transacao;
import com.andersonfariasdev.contabancariaapi.domain.model.enums.TipoPessoa;
import com.andersonfariasdev.contabancariaapi.domain.repository.TransacaoRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.annotation.DirtiesContext;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.transaction.PlatformTransactionManager;
import org.springframework.transaction.support.TransactionTemplate;

import java.math.BigDecimal;
import java.util.concurrent.atomic.AtomicInteger;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.doThrow;

@SpringBootTest
@ActiveProfiles("test")
@DirtiesContext(classMode = DirtiesContext.ClassMode.AFTER_EACH_TEST_METHOD)
class ContaBancariaMovimentacaoConsistenciaIT {

    @Autowired
    ContaBancariaService contaBancariaService;

    @Autowired
    JpaContaBancariaRepository contaRepo;

    @Autowired
    JpaClienteRepository clienteRepo;

    @MockitoBean
    TransacaoRepository transacaoRepository;

    @Autowired
    PlatformTransactionManager transactionManager;

    @BeforeEach
    void seed() {
        var coop = ClienteJpaEntity.builder()
                .nomeRazao("Titular Rollback")
                .documento("613.443.940-19")
                .tipo(TipoPessoa.PF)
                .build();
        clienteRepo.save(coop);

        var conta = new ContaBancariaJpaEntity();
        conta.setNumero("RBX");
        conta.setDigitoVerificador("1");
        conta.setSaldo(new BigDecimal("500.00"));
        conta.setTitular(coop);
        contaRepo.save(conta);
    }

    @Test
    void depositoFazRollbackSePersistenciaDeTransacaoFalhar() {
        doThrow(new RuntimeException("falha ao gravar transação")).when(transacaoRepository).save(any(Transacao.class));

        assertThrows(RuntimeException.class, () -> contaBancariaService.depositar("RBX", new BigDecimal("100.00")));

        var saldo = new TransactionTemplate(transactionManager).execute(status ->
                contaRepo.findByNumero("RBX").orElseThrow().getSaldo()
        );
        assertEquals(0, saldo.compareTo(new BigDecimal("500.00")));
    }

    @Test
    void transferenciaFazRollbackSeSegundaTransacaoFalhar() {
        var coop2 = ClienteJpaEntity.builder()
                .nomeRazao("B")
                .documento("589.414.860-09")
                .tipo(TipoPessoa.PF)
                .build();
        clienteRepo.save(coop2);
        var contaB = new ContaBancariaJpaEntity();
        contaB.setNumero("RBY");
        contaB.setDigitoVerificador("1");
        contaB.setSaldo(new BigDecimal("100.00"));
        contaB.setTitular(coop2);
        contaRepo.save(contaB);

        AtomicInteger chamadas = new AtomicInteger();
        org.mockito.Mockito.doAnswer(inv -> {
            if (chamadas.incrementAndGet() >= 2) {
                throw new RuntimeException("falha na segunda transação");
            }
            return inv.getArgument(0);
        }).when(transacaoRepository).save(any(Transacao.class));

        assertThrows(RuntimeException.class,
                () -> contaBancariaService.transferir(new TransferenciaRequest("RBX", "RBY", new BigDecimal("50.00"))));

        new TransactionTemplate(transactionManager).executeWithoutResult(status -> {
            assertEquals(0, contaRepo.findByNumero("RBX").orElseThrow().getSaldo().compareTo(new BigDecimal("500.00")));
            assertEquals(0, contaRepo.findByNumero("RBY").orElseThrow().getSaldo().compareTo(new BigDecimal("100.00")));
        });
    }
}
