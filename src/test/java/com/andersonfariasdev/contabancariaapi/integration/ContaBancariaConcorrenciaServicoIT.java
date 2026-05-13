package com.andersonfariasdev.contabancariaapi.integration;

import com.andersonfariasdev.contabancariaapi.adapters.inbound.dto.TransferenciaRequest;
import com.andersonfariasdev.contabancariaapi.adapters.outbound.entities.ContaBancariaJpaEntity;
import com.andersonfariasdev.contabancariaapi.adapters.outbound.entities.CooperadoJpaEntity;
import com.andersonfariasdev.contabancariaapi.adapters.outbound.repository.jpa.JpaContaBancariaRepository;
import com.andersonfariasdev.contabancariaapi.adapters.outbound.repository.jpa.JpaCooperadoRepository;
import com.andersonfariasdev.contabancariaapi.application.service.ContaBancariaService;
import com.andersonfariasdev.contabancariaapi.domain.model.enums.CooperadoType;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.annotation.DirtiesContext;
import org.springframework.test.context.ActiveProfiles;

import java.math.BigDecimal;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicInteger;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

@SpringBootTest
@ActiveProfiles("test")
@DirtiesContext(classMode = DirtiesContext.ClassMode.AFTER_EACH_TEST_METHOD)
class ContaBancariaConcorrenciaServicoIT {

    private static final int THREADS = 40;
    private static final int TRANSFERENCIAS_POR_THREAD = 25;

    @Autowired
    ContaBancariaService contaBancariaService;

    @Autowired
    JpaContaBancariaRepository contaRepo;

    @Autowired
    JpaCooperadoRepository cooperadoRepo;

    @BeforeEach
    void seed() {
        var c1 = CooperadoJpaEntity.builder().nomeRazao("C1").documento("613.443.940-19").tipo(CooperadoType.PF).build();
        var c2 = CooperadoJpaEntity.builder().nomeRazao("C2").documento("589.414.860-09").tipo(CooperadoType.PF).build();
        cooperadoRepo.save(c1);
        cooperadoRepo.save(c2);

        var a = new ContaBancariaJpaEntity();
        a.setNumero("HV_A");
        a.setDigitoVerificador("1");
        a.setSaldo(new BigDecimal("5000.00"));
        a.setTitular(c1);
        var b = new ContaBancariaJpaEntity();
        b.setNumero("HV_B");
        b.setDigitoVerificador("1");
        b.setSaldo(new BigDecimal("1000.00"));
        b.setTitular(c2);
        contaRepo.save(a);
        contaRepo.save(b);
    }

    @Test
    void muitasTransferenciasConcorrentesViaServicoMantemConsistenciaDeSaldo() throws InterruptedException {
        var req = new TransferenciaRequest("HV_A", "HV_B", new BigDecimal("1.00"));
        ExecutorService pool = Executors.newFixedThreadPool(THREADS);
        CountDownLatch start = new CountDownLatch(1);
        CountDownLatch done = new CountDownLatch(THREADS);
        AtomicInteger falhas = new AtomicInteger();

        for (int t = 0; t < THREADS; t++) {
            pool.submit(() -> {
                try {
                    start.await();
                    for (int i = 0; i < TRANSFERENCIAS_POR_THREAD; i++) {
                        contaBancariaService.transferir(req);
                    }
                } catch (Exception e) {
                    falhas.incrementAndGet();
                } finally {
                    done.countDown();
                }
            });
        }

        start.countDown();
        assertTrue(done.await(120, TimeUnit.SECONDS), "threads não finalizaram a tempo");
        pool.shutdown();
        assertTrue(pool.awaitTermination(30, TimeUnit.SECONDS));

        int totalOps = THREADS * TRANSFERENCIAS_POR_THREAD;
        assertEquals(0, falhas.get(), "não deveria haver falhas de transferência");

        var saldoA = contaRepo.findByNumero("HV_A").orElseThrow().getSaldo();
        var saldoB = contaRepo.findByNumero("HV_B").orElseThrow().getSaldo();
        var esperadoA = new BigDecimal("5000.00").subtract(new BigDecimal(totalOps));
        var esperadoB = new BigDecimal("1000.00").add(new BigDecimal(totalOps));
        assertEquals(0, saldoA.compareTo(esperadoA));
        assertEquals(0, saldoB.compareTo(esperadoB));
    }

    @Test
    void muitosDepositosConcorrentesNaMesmaConta() throws InterruptedException {
        var coop = cooperadoRepo.findByDocumento("613.443.940-19").orElseThrow();
        var conta = new ContaBancariaJpaEntity();
        conta.setNumero("HV_D");
        conta.setDigitoVerificador("1");
        conta.setSaldo(BigDecimal.ZERO);
        conta.setTitular(coop);
        contaRepo.save(conta);

        int threads = 30;
        int depositosPorThread = 20;
        ExecutorService pool = Executors.newFixedThreadPool(threads);
        CountDownLatch start = new CountDownLatch(1);
        CountDownLatch done = new CountDownLatch(threads);

        for (int i = 0; i < threads; i++) {
            pool.submit(() -> {
                try {
                    start.await();
                    for (int j = 0; j < depositosPorThread; j++) {
                        contaBancariaService.depositar("HV_D", new BigDecimal("5.00"));
                    }
                } catch (InterruptedException e) {
                    Thread.currentThread().interrupt();
                } finally {
                    done.countDown();
                }
            });
        }

        start.countDown();
        assertTrue(done.await(120, TimeUnit.SECONDS));
        pool.shutdown();
        assertTrue(pool.awaitTermination(30, TimeUnit.SECONDS));

        var saldo = contaRepo.findByNumero("HV_D").orElseThrow().getSaldo();
        assertEquals(0, saldo.compareTo(new BigDecimal("3000.00")));
    }
}
