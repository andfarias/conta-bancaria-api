package com.andersonfariasdev.contabancariaapi.integration;

import com.andersonfariasdev.contabancariaapi.adapters.inbound.dto.TransferenciaRequest;
import com.andersonfariasdev.contabancariaapi.adapters.outbound.entities.ContaBancariaJpaEntity;
import com.andersonfariasdev.contabancariaapi.adapters.outbound.entities.CooperadoJpaEntity;
import com.andersonfariasdev.contabancariaapi.adapters.outbound.repository.jpa.JpaContaBancariaRepository;
import com.andersonfariasdev.contabancariaapi.domain.model.enums.CooperadoType;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.web.server.LocalServerPort;
import org.springframework.test.annotation.DirtiesContext;
import org.springframework.test.context.ActiveProfiles;

import java.math.BigDecimal;
import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpRequest.BodyPublishers;
import java.net.http.HttpResponse;
import java.net.http.HttpResponse.BodyHandlers;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.TimeUnit;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
@ActiveProfiles("test")
@DirtiesContext(classMode = DirtiesContext.ClassMode.AFTER_EACH_TEST_METHOD)
public class TransferenciaConcorrenteTest {

    HttpClient client;
    ObjectMapper objectMapper = new ObjectMapper();

    @LocalServerPort
    int port;

    @Autowired
    JpaContaBancariaRepository contaRepo;

    @Autowired
    com.andersonfariasdev.contabancariaapi.adapters.outbound.repository.jpa.JpaCooperadoRepository cooperadoRepo;

    @BeforeEach
    void setup() {
        var coop1 = CooperadoJpaEntity.builder()
                .nomeRazao("Coop A")
                .documento("613.443.940-19")
                .tipo(CooperadoType.PF)
                .build();
        var coop2 = CooperadoJpaEntity.builder()
                .nomeRazao("Coop B")
                .documento("589.414.860-09")
                .tipo(CooperadoType.PF)
                .build();
        cooperadoRepo.save(coop1);
        cooperadoRepo.save(coop2);

        var c1 = new ContaBancariaJpaEntity();
        c1.setNumero("A");
        c1.setDigitoVerificador("1");
        c1.setSaldo(new BigDecimal("1000.00"));
        c1.setTitular(coop1);
        var c2 = new ContaBancariaJpaEntity();
        c2.setNumero("B");
        c2.setDigitoVerificador("1");
        c2.setSaldo(new BigDecimal("1000.00"));
        c2.setTitular(coop2);
        contaRepo.save(c1);
        contaRepo.save(c2);
        this.client = HttpClient.newHttpClient();
    }

    @Test
    void transferenciasConcorrentes() throws InterruptedException {
        int threads = 10;
        ExecutorService es = Executors.newFixedThreadPool(threads);
        CountDownLatch latch = new CountDownLatch(threads);

        for (int i = 0; i < threads; i++) {
            es.submit(() -> {
                try {
                    var req = new TransferenciaRequest("A", "B", new BigDecimal("10.00"));

                    try {
                        String json = objectMapper.writeValueAsString(req);
                        HttpRequest httpReq = HttpRequest.newBuilder()
                                .uri(URI.create("http://localhost:" + port + "/api/contas/transferencia"))
                                .header("Content-Type", "application/json")
                                .POST(BodyPublishers.ofString(json))
                                .build();

                        HttpResponse<String> response = client.send(httpReq, BodyHandlers.ofString());
                        if (response.statusCode() >= 300) {
                            throw new RuntimeException("Unexpected status: " + response.statusCode() + " body: " + response.body());
                        }
                    } catch (Exception e) {
                        throw new RuntimeException(e);
                    }
                } finally {
                    latch.countDown();
                }
            });
        }

        latch.await();

        var a = contaRepo.findByNumero("A").get();
        var b = contaRepo.findByNumero("B").get();

        assertEquals(new BigDecimal("900.00"), a.getSaldo());
        assertEquals(new BigDecimal("1100.00"), b.getSaldo());
    }

    @Test
    void transferenciasHttpAltoVolume() throws InterruptedException {
        int threads = 40;
        int chamadasPorThread = 15;
        ExecutorService es = Executors.newFixedThreadPool(threads);
        CountDownLatch latch = new CountDownLatch(threads * chamadasPorThread);

        for (int t = 0; t < threads; t++) {
            es.submit(() -> {
                for (int k = 0; k < chamadasPorThread; k++) {
                    try {
                        var req = new TransferenciaRequest("A", "B", new BigDecimal("0.50"));
                        String json = objectMapper.writeValueAsString(req);
                        HttpRequest httpReq = HttpRequest.newBuilder()
                                .uri(URI.create("http://localhost:" + port + "/api/contas/transferencia"))
                                .header("Content-Type", "application/json")
                                .POST(BodyPublishers.ofString(json))
                                .build();
                        HttpResponse<String> response = client.send(httpReq, BodyHandlers.ofString());
                        if (response.statusCode() >= 300) {
                            throw new RuntimeException("HTTP " + response.statusCode() + " " + response.body());
                        }
                    } catch (Exception e) {
                        throw new RuntimeException(e);
                    } finally {
                        latch.countDown();
                    }
                }
            });
        }

        assertTrue(latch.await(180, TimeUnit.SECONDS));
        es.shutdown();
        assertTrue(es.awaitTermination(60, TimeUnit.SECONDS));

        int total = threads * chamadasPorThread;
        var a = contaRepo.findByNumero("A").get();
        var b = contaRepo.findByNumero("B").get();
        assertEquals(0, a.getSaldo().compareTo(new BigDecimal("1000.00").subtract(new BigDecimal("0.50").multiply(new BigDecimal(total)))));
        assertEquals(0, b.getSaldo().compareTo(new BigDecimal("1000.00").add(new BigDecimal("0.50").multiply(new BigDecimal(total)))));
    }

    @Test
    void depositosHttpConcorrentesNaMesmaConta() throws InterruptedException {
        var c3 = CooperadoJpaEntity.builder()
                .nomeRazao("Coop C")
                .documento("11.222.333/0001-81")
                .tipo(CooperadoType.PJ)
                .build();
        cooperadoRepo.save(c3);
        var cDep = new ContaBancariaJpaEntity();
        cDep.setNumero("C");
        cDep.setDigitoVerificador("1");
        cDep.setSaldo(BigDecimal.ZERO);
        cDep.setTitular(c3);
        contaRepo.save(cDep);

        int threads = 25;
        int porThread = 12;
        ExecutorService es = Executors.newFixedThreadPool(threads);
        CountDownLatch latch = new CountDownLatch(threads * porThread);

        for (int t = 0; t < threads; t++) {
            es.submit(() -> {
                for (int k = 0; k < porThread; k++) {
                    try {
                        String json = "{\"numeroConta\":\"C\",\"valor\":2.00}";
                        HttpRequest httpReq = HttpRequest.newBuilder()
                                .uri(URI.create("http://localhost:" + port + "/api/contas/deposito"))
                                .header("Content-Type", "application/json")
                                .POST(BodyPublishers.ofString(json))
                                .build();
                        HttpResponse<String> response = client.send(httpReq, BodyHandlers.ofString());
                        if (response.statusCode() >= 300) {
                            throw new RuntimeException("HTTP " + response.statusCode());
                        }
                    } catch (Exception e) {
                        throw new RuntimeException(e);
                    } finally {
                        latch.countDown();
                    }
                }
            });
        }

        assertTrue(latch.await(180, TimeUnit.SECONDS));
        es.shutdown();
        assertTrue(es.awaitTermination(60, TimeUnit.SECONDS));

        var saldo = contaRepo.findByNumero("C").get().getSaldo();
        assertEquals(0, saldo.compareTo(new BigDecimal("600.00")));
    }
}
