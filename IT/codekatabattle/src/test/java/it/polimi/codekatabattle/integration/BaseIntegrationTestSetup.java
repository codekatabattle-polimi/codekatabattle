package it.polimi.codekatabattle.integration;

import io.restassured.RestAssured;
import org.junit.jupiter.api.AfterAll;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.BeforeEach;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.test.web.server.LocalServerPort;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.test.context.DynamicPropertyRegistry;
import org.springframework.test.context.DynamicPropertySource;
import org.testcontainers.containers.PostgreSQLContainer;

public class BaseIntegrationTestSetup {

    @Value("${ckb.github.test.pat}")
    protected String personalAccessToken;

    @LocalServerPort
    private Integer port;

    private final JpaRepository<?, ?> repository;

    @DynamicPropertySource
    static void setDynamicProperties(DynamicPropertyRegistry registry) {
        registry.add("ckb.test", () -> true);
    }

    @BeforeEach
    void setup() {
        RestAssured.baseURI = "http://localhost:" + port;
        this.repository.deleteAll();
    }

    public BaseIntegrationTestSetup(JpaRepository<?, ?> repository) {
        this.repository = repository;
    }

}
