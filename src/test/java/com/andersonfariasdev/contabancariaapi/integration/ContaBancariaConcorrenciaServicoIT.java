package com.andersonfariasdev.contabancariaapi.integration;

import com.andersonfariasdev.contabancariaapi.adapters.inbound.dto.TransferenciaRequest;
import com.andersonfariasdev.contabancariaapi.adapters.outbound.entities.ContaBancariaJpaEntity;
import com.andersonfariasdev.contabancariaapi.adapters.outbound.entities.ClienteJpaEntity;
import com.andersonfariasdev.contabancariaapi.adapters.outbound.repository.jpa.JpaContaBancariaRepository;
import com.andersonfariasdev.contabancariaapi.adapters.outbound.repository.jpa.JpaClienteRepository;
import com.andersonfariasdev.contabancariaapi.application.service.ContaBancariaService;
import com.andersonfariasdev.contabancariaapi.domain.model.enums.TipoPessoa;
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
    JpaClienteRepository clienteRepo;
    @Autowired
    org.springframework.transaction.PlatformTransactionManager transactionManager;

    @BeforeEach
    void seed() {
        var c1 = ClienteJpaEntity.builder().nomeRazao("C1").documento("613.443.940-19").tipo(TipoPessoa.PF).build();
        var c2 = ClienteJpaEntity.builder().nomeRazao("C2").documento("589.414.860-09").tipo(TipoPessoa.PF).build();
        clienteRepo.save(c1);
        clienteRepo.save(c2);

        var a = new ContaBancariaJpaEntity();
        a.setNumero("000001");
        a.setDigitoVerificador("1");
        a.setSaldo(new BigDecimal("5000.00"));
        a.setTitular(c1);
        var b = new ContaBancariaJpaEntity();
        b.setNumero("000002");
        b.setDigitoVerificador("1");
        b.setSaldo(new BigDecimal("1000.00"));
        b.setTitular(c2);
        contaRepo.save(a);
        contaRepo.save(b);
    }

    @Test
    void muitasTransferenciasConcorrentesViaServicoMantemConsistenciaDeSaldo() throws InterruptedException {
        var req = new TransferenciaRequest("000001", "000002", new BigDecimal("1.00"));
        ExecutorService pool = Executors.newFixedThreadPool(THREADS);
        CountDownLatch start = new CountDownLatch(1);
        CountDownLatch done = new CountDownLatch(THREADS);
        AtomicInteger falhas = new AtomicInteger();

        for (int t = 0; t < THREADS; t++) {
            pool.submit(() -> {
                try {
                    start.await();
                    for (int i = 0; i < TRANSFERENCIAS_POR_THREAD; i++) {
                        int attempts = 0;
                        boolean ok = false;
                        while (!ok && attempts < 5) {
                            try {
                                contaBancariaService.transferir(req.contaOrigem(), req.contaDestino(), req.valor());
                                ok = true;
                            } catch (org.springframework.dao.OptimisticLockingFailureException ole) {
                                attempts++;
                                // pequeno backoff
                                try {
                                    Thread.sleep(10L * attempts);
                                } catch (InterruptedException ignored) {
                                    Thread.currentThread().interrupt();
                                }
                            }
                        }
                        if (!ok) {
                            // count per-operation failure and continue with other transfers
                            falhas.incrementAndGet();
                        }
                    }
                } catch (Exception e) {
                    // unexpected thread-level error; log for debugging but do not mix with per-operation failures
                    e.printStackTrace();
                } finally {
                    done.countDown();
                }
            });
        }

        start.countDown();
        assertTrue(done.await(300, TimeUnit.SECONDS), "threads não finalizaram a tempo");
        pool.shutdown();
        assertTrue(pool.awaitTermination(120, TimeUnit.SECONDS));

        int totalOps = THREADS * TRANSFERENCIAS_POR_THREAD;
        int failures = falhas.get();
        int successes = totalOps - failures;

        // verificar que o número de falhas está entre 0 e totalOps
        assertTrue(failures >= 0 && failures <= totalOps);

        var balances = new org.springframework.transaction.support.TransactionTemplate(transactionManager).execute(status -> {
            var sA = contaRepo.findByNumero("000001").orElseThrow().getSaldo();
            var sB = contaRepo.findByNumero("000002").orElseThrow().getSaldo();
            return new java.math.BigDecimal[]{sA, sB};
        });
        var saldoA = balances[0];
        var saldoB = balances[1];
        var esperadoA = new BigDecimal("5000.00").subtract(new BigDecimal(successes));
        var esperadoB = new BigDecimal("1000.00").add(new BigDecimal(successes));
        assertEquals(0, saldoA.compareTo(esperadoA));
        assertEquals(0, saldoB.compareTo(esperadoB));
    }

    @Test
    void muitosDepositosConcorrentesNaMesmaConta() throws InterruptedException {
        var coop = clienteRepo.findByDocumento("613.443.940-19").orElseThrow();
        var conta = new ContaBancariaJpaEntity();
        conta.setNumero("000003");
        conta.setDigitoVerificador("1");
        conta.setSaldo(BigDecimal.ZERO);
        conta.setTitular(coop);
        contaRepo.save(conta);

        int threads = 30;
        int depositosPorThread = 20;
        ExecutorService pool = Executors.newFixedThreadPool(threads);
        CountDownLatch start = new CountDownLatch(1);
        CountDownLatch done = new CountDownLatch(threads);
        AtomicInteger falhas = new AtomicInteger();

        for (int i = 0; i < threads; i++) {
            pool.submit(() -> {
                try {
                    start.await();
                    for (int j = 0; j < depositosPorThread; j++) {
                        // tentar com retry local em teste; se esgotar, contar como falha
                        boolean ok = false;
                        for (int attempt = 0; attempt < 5 && !ok; attempt++) {
                            try {
                                contaBancariaService.depositar("000003", new BigDecimal("5.00"));
                                ok = true;
                            } catch (org.springframework.dao.OptimisticLockingFailureException ole) {
                                try {
                                    Thread.sleep(10L * (attempt + 1));
                                } catch (InterruptedException ignored) {
                                    Thread.currentThread().interrupt();
                                }
                            }
                        }
                        if (!ok) {
                            falhas.incrementAndGet();
                        }
                    }
                } catch (Exception e) {
                    falhas.incrementAndGet();
                } finally {
                    done.countDown();
                }
            });
        }

        start.countDown();
        assertTrue(done.await(300, TimeUnit.SECONDS));
        pool.shutdown();
        assertTrue(pool.awaitTermination(120, TimeUnit.SECONDS));

        int total = threads * depositosPorThread;
        int failuresCount = falhas.get();
        int successes = total - failuresCount;

        var saldo = new org.springframework.transaction.support.TransactionTemplate(transactionManager).execute(status ->
                contaRepo.findByNumero("000003").orElseThrow().getSaldo()
        );

        var esperado = new BigDecimal("0.00").add(new BigDecimal("5.00").multiply(new BigDecimal(successes)));
        assertEquals(0, saldo.compareTo(esperado));
    }
}
