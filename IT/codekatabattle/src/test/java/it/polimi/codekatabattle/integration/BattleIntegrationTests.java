package it.polimi.codekatabattle.integration;

import io.restassured.http.ContentType;
import it.polimi.codekatabattle.entities.*;
import it.polimi.codekatabattle.exceptions.OAuthException;
import it.polimi.codekatabattle.models.BattleTest;
import it.polimi.codekatabattle.models.dto.BattleDTO;
import it.polimi.codekatabattle.models.dto.BattleEntryDTO;
import it.polimi.codekatabattle.models.dto.TournamentDTO;
import it.polimi.codekatabattle.models.github.GHUser;
import it.polimi.codekatabattle.models.oauth.AuthOrigin;
import it.polimi.codekatabattle.repositories.BattleEntryRepository;
import it.polimi.codekatabattle.repositories.BattleRepository;
import it.polimi.codekatabattle.services.AuthService;
import it.polimi.codekatabattle.services.BattleService;
import it.polimi.codekatabattle.services.TournamentService;
import it.polimi.codekatabattle.utils.clock.ConfigurableClock;
import jakarta.persistence.EntityNotFoundException;
import org.junit.jupiter.api.AfterAll;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;
import org.kohsuke.github.GHRepository;
import org.kohsuke.github.GitHub;
import org.kohsuke.github.GitHubBuilder;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.HttpStatus;
import org.springframework.test.context.DynamicPropertyRegistry;
import org.springframework.test.context.DynamicPropertySource;
import org.testcontainers.containers.PostgreSQLContainer;

import java.io.IOException;
import java.time.Clock;
import java.time.Duration;
import java.time.LocalDateTime;
import java.util.List;

import static io.restassured.RestAssured.given;
import static java.util.concurrent.TimeUnit.MINUTES;
import static org.awaitility.Awaitility.await;
import static org.hamcrest.CoreMatchers.equalTo;
import static org.hamcrest.MatcherAssert.assertThat;
import static org.junit.jupiter.api.Assertions.assertDoesNotThrow;
import static org.junit.jupiter.api.Assertions.assertNotNull;

@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
public class BattleIntegrationTests extends BaseIntegrationTestSetup {

    static PostgreSQLContainer<?> postgres = new PostgreSQLContainer<>(
        "postgres:15-alpine"
    );

    @BeforeAll
    static void beforeAll() {
        postgres.start();
    }

    @AfterAll
    static void afterAll() {
        postgres.stop();
    }

    @DynamicPropertySource
    static void configureProperties(DynamicPropertyRegistry registry) {
        registry.add("spring.datasource.url", postgres::getJdbcUrl);
        registry.add("spring.datasource.username", postgres::getUsername);
        registry.add("spring.datasource.password", postgres::getPassword);
    }

    @Autowired
    private AuthService authService;

    @Autowired
    private TournamentService tournamentService;

    @Autowired
    private BattleService battleService;

    @Autowired
    private BattleEntryRepository battleEntryRepository;

    @Autowired
    private ConfigurableClock clock;

    public BattleIntegrationTests(@Autowired BattleRepository tournamentRepository) {
        super(tournamentRepository);
    }

    @Test
    void givenCreatedTournament_shouldCreateBattle() throws OAuthException, IOException {
        TournamentDTO tournamentDTO = new TournamentDTO();
        tournamentDTO.setTitle("Tournament 1");
        tournamentDTO.setDescription("Tournament Description 1");
        tournamentDTO.setPrivacy(TournamentPrivacy.PUBLIC);
        tournamentDTO.setStartsAt(LocalDateTime.now(clock.getClock()).plusHours(1));
        tournamentDTO.setEndsAt(LocalDateTime.now(clock.getClock()).plusDays(5));
        tournamentDTO.setMaxParticipants(100);

        GHUser user = this.authService.getUserInfo(personalAccessToken, AuthOrigin.SWAGGER);
        Tournament tournament = this.tournamentService.create(tournamentDTO, user);

        BattleDTO battleDTO = new BattleDTO();
        battleDTO.setTournamentId(tournament.getId());
        battleDTO.setTitle("FizzBuzz");
        battleDTO.setDescription("Implement FizzBuzz algorithm. Example output: 10 -> 1-2-Fizz-4-Buzz-6-7-8-Fizz-Buzz");
        battleDTO.setStartsAt(LocalDateTime.now(clock.getClock()).plusHours(1));
        battleDTO.setEndsAt(LocalDateTime.now(clock.getClock()).plusDays(5));
        battleDTO.setLanguage(BattleLanguage.GOLANG);
        battleDTO.setEnableSAT(true);
        battleDTO.setEnableManualEvaluation(true);
        battleDTO.setTimelinessBaseScore(100);

        BattleTest test1 = new BattleTest();
        test1.setName("Test 1");
        test1.setPrivacy(BattleTestPrivacy.PUBLIC);
        test1.setInput("3");
        test1.setExpectedOutput("1-2-Fizz");
        test1.setGivesScore(5);

        BattleTest test2 = new BattleTest();
        test2.setName("Test 2");
        test2.setPrivacy(BattleTestPrivacy.PUBLIC);
        test2.setInput("5");
        test2.setExpectedOutput("1-2-Fizz-4-Buzz");
        test1.setGivesScore(5);

        BattleTest test3 = new BattleTest();
        test3.setName("Test 3");
        test3.setPrivacy(BattleTestPrivacy.PRIVATE);
        test3.setInput("15");
        test3.setExpectedOutput("1-2-Fizz-4-Buzz-6-7-8-Fizz-Buzz-11-Fizz-13-14-FizzBuzz");
        test3.setGivesScore(100);

        battleDTO.setTests(List.of(test1, test2, test3));

        GHUser creator = this.authService.getUserInfo(personalAccessToken, AuthOrigin.SWAGGER);
        Battle battle = this.battleService.create(battleDTO, creator);

        assertThat(battle.getTournament().getId(), equalTo(tournament.getId()));
        assertNotNull(battle.getRepositoryId());

        GitHub github = new GitHubBuilder().withOAuthToken(personalAccessToken).build();
        GHRepository repository = github.getRepositoryById(battle.getRepositoryId());

        assertNotNull(repository);
        assertThat(repository.getName(), equalTo("fizzbuzz"));
        assertThat(repository.getDescription(), equalTo(battle.getDescription()));

        this.battleService.deleteById(battle.getId(), creator);
    }

    @Test
    void givenCreatedTournamentAndBattle_shouldGetByIdNotIncludingPrivateTests() throws OAuthException, IOException {
        TournamentDTO tournamentDTO = new TournamentDTO();
        tournamentDTO.setTitle("Tournament 1");
        tournamentDTO.setDescription("Tournament Description 1");
        tournamentDTO.setPrivacy(TournamentPrivacy.PUBLIC);
        tournamentDTO.setStartsAt(LocalDateTime.now(clock.getClock()).plusHours(1));
        tournamentDTO.setEndsAt(LocalDateTime.now(clock.getClock()).plusDays(5));
        tournamentDTO.setMaxParticipants(100);

        GHUser user = this.authService.getUserInfo(personalAccessToken, AuthOrigin.SWAGGER);
        Tournament tournament = this.tournamentService.create(tournamentDTO, user);

        BattleDTO battleDTO = new BattleDTO();
        battleDTO.setTournamentId(tournament.getId());
        battleDTO.setTitle("FizzBuzz 2");
        battleDTO.setDescription("Implement FizzBuzz algorithm. Example output: 10 -> 1-2-Fizz-4-Buzz-6-7-8-Fizz-Buzz");
        battleDTO.setStartsAt(LocalDateTime.now(clock.getClock()).plusHours(1));
        battleDTO.setEndsAt(LocalDateTime.now(clock.getClock()).plusDays(5));
        battleDTO.setLanguage(BattleLanguage.GOLANG);
        battleDTO.setEnableSAT(true);
        battleDTO.setEnableManualEvaluation(true);
        battleDTO.setTimelinessBaseScore(100);

        BattleTest test1 = new BattleTest();
        test1.setName("Test 1");
        test1.setPrivacy(BattleTestPrivacy.PUBLIC);
        test1.setInput("3");
        test1.setExpectedOutput("1-2-Fizz");
        test1.setGivesScore(5);

        BattleTest test2 = new BattleTest();
        test2.setName("Test 2");
        test2.setPrivacy(BattleTestPrivacy.PUBLIC);
        test2.setInput("5");
        test2.setExpectedOutput("1-2-Fizz-4-Buzz");
        test1.setGivesScore(5);

        BattleTest test3 = new BattleTest();
        test3.setName("Test 3");
        test3.setPrivacy(BattleTestPrivacy.PRIVATE);
        test3.setInput("15");
        test3.setExpectedOutput("1-2-Fizz-4-Buzz-6-7-8-Fizz-Buzz-11-Fizz-13-14-FizzBuzz");
        test3.setGivesScore(100);

        battleDTO.setTests(List.of(test1, test2, test3));

        GHUser creator = this.authService.getUserInfo(personalAccessToken, AuthOrigin.SWAGGER);
        Battle battle = this.battleService.create(battleDTO, creator);

        // Check that the private test is not included
        given()
            .contentType(ContentType.JSON)
            .headers("Authorization", "Bearer " + personalAccessToken)
            .when()
            .get("/battles/" + battle.getId())
            .then()
            .statusCode(HttpStatus.OK.value())
            .body("tests.size()", equalTo(2));

        this.battleService.deleteById(battle.getId(), creator);
    }

    @Test
    void givenCreatedTournamentAndBattle_shouldGetPageNotIncludingPrivateTests() throws IOException, OAuthException {
        TournamentDTO tournamentDTO = new TournamentDTO();
        tournamentDTO.setTitle("Tournament 1");
        tournamentDTO.setDescription("Tournament Description 1");
        tournamentDTO.setPrivacy(TournamentPrivacy.PUBLIC);
        tournamentDTO.setStartsAt(LocalDateTime.now(clock.getClock()).plusHours(1));
        tournamentDTO.setEndsAt(LocalDateTime.now(clock.getClock()).plusDays(5));
        tournamentDTO.setMaxParticipants(100);

        GHUser user = this.authService.getUserInfo(personalAccessToken, AuthOrigin.SWAGGER);
        Tournament tournament = this.tournamentService.create(tournamentDTO, user);

        BattleDTO battleDTO = new BattleDTO();
        battleDTO.setTournamentId(tournament.getId());
        battleDTO.setTitle("FizzBuzz 3");
        battleDTO.setDescription("Implement FizzBuzz algorithm. Example output: 10 -> 1-2-Fizz-4-Buzz-6-7-8-Fizz-Buzz");
        battleDTO.setStartsAt(LocalDateTime.now(clock.getClock()).plusHours(1));
        battleDTO.setEndsAt(LocalDateTime.now(clock.getClock()).plusDays(5));
        battleDTO.setLanguage(BattleLanguage.GOLANG);
        battleDTO.setEnableSAT(true);
        battleDTO.setEnableManualEvaluation(true);
        battleDTO.setTimelinessBaseScore(100);

        BattleTest test1 = new BattleTest();
        test1.setName("Test 1");
        test1.setPrivacy(BattleTestPrivacy.PUBLIC);
        test1.setInput("3");
        test1.setExpectedOutput("1-2-Fizz");
        test1.setGivesScore(5);

        BattleTest test2 = new BattleTest();
        test2.setName("Test 2");
        test2.setPrivacy(BattleTestPrivacy.PUBLIC);
        test2.setInput("5");
        test2.setExpectedOutput("1-2-Fizz-4-Buzz");
        test1.setGivesScore(5);

        BattleTest test3 = new BattleTest();
        test3.setName("Test 3");
        test3.setPrivacy(BattleTestPrivacy.PRIVATE);
        test3.setInput("15");
        test3.setExpectedOutput("1-2-Fizz-4-Buzz-6-7-8-Fizz-Buzz-11-Fizz-13-14-FizzBuzz");
        test3.setGivesScore(100);

        battleDTO.setTests(List.of(test1, test2, test3));

        GHUser creator = this.authService.getUserInfo(personalAccessToken, AuthOrigin.SWAGGER);
        Battle battle = this.battleService.create(battleDTO, creator);

        // Check that the private test is not included
        given()
            .contentType(ContentType.JSON)
            .headers("Authorization", "Bearer " + personalAccessToken)
            .when()
            .get("/battles?page=0&size=10")
            .then()
            .statusCode(HttpStatus.OK.value())
            .body("content[0].tests.size()", equalTo(2));

        this.battleService.deleteById(battle.getId(), creator);
    }

    @Test
    void givenCreatedTournamentAndBattle_shouldJoinBattle() throws OAuthException, IOException {
        TournamentDTO tournamentDTO = new TournamentDTO();
        tournamentDTO.setTitle("Tournament 1");
        tournamentDTO.setDescription("Tournament Description 1");
        tournamentDTO.setPrivacy(TournamentPrivacy.PUBLIC);
        tournamentDTO.setStartsAt(LocalDateTime.now(clock.getClock()).plusDays(1));
        tournamentDTO.setEndsAt(LocalDateTime.now(clock.getClock()).plusDays(30));
        tournamentDTO.setMaxParticipants(100);

        GHUser creator = new GHUser();
        creator.setLogin("creator1");
        Tournament tournament = this.tournamentService.create(tournamentDTO, creator);

        GHUser joiner = this.authService.getUserInfo(personalAccessToken, AuthOrigin.SWAGGER);

        this.tournamentService.join(tournament.getId(), joiner);

        clock.setClock(Clock.offset(clock.getClock(), Duration.ofDays(2)));

        BattleDTO battleDTO = new BattleDTO();
        battleDTO.setTournamentId(tournament.getId());
        battleDTO.setTitle("FizzBuzz 4");
        battleDTO.setDescription("Implement FizzBuzz algorithm. Example output: 10 -> 1-2-Fizz-4-Buzz-6-7-8-Fizz-Buzz");
        battleDTO.setStartsAt(LocalDateTime.now(clock.getClock()).minusHours(1));
        battleDTO.setEndsAt(LocalDateTime.now(clock.getClock()).plusDays(5));
        battleDTO.setLanguage(BattleLanguage.GOLANG);
        battleDTO.setEnableSAT(true);
        battleDTO.setEnableManualEvaluation(true);
        battleDTO.setTimelinessBaseScore(100);

        Battle battle = this.battleService.create(battleDTO, creator);

        given()
            .contentType(ContentType.JSON)
            .headers("Authorization", "Bearer " + personalAccessToken)
            .when()
            .put("/battles/" + battle.getId() + "/join")
            .then()
            .statusCode(HttpStatus.OK.value())
            .body("participants.size()", equalTo(1))
            .body("participants[0].username", equalTo(joiner.getLogin()));

        this.battleService.deleteById(battle.getId(), creator);
    }

    @Test
    void givenCreatedTournamentAndBattle_shouldBlockJoinIfBattleHasNotStarted() throws OAuthException, IOException {
        TournamentDTO tournamentDTO = new TournamentDTO();
        tournamentDTO.setTitle("Tournament 1");
        tournamentDTO.setDescription("Tournament Description 1");
        tournamentDTO.setPrivacy(TournamentPrivacy.PUBLIC);
        tournamentDTO.setStartsAt(LocalDateTime.now(clock.getClock()).plusDays(1));
        tournamentDTO.setEndsAt(LocalDateTime.now(clock.getClock()).plusDays(30));
        tournamentDTO.setMaxParticipants(100);

        GHUser creator = new GHUser();
        creator.setLogin("creator1");
        Tournament tournament = this.tournamentService.create(tournamentDTO, creator);

        GHUser joiner = this.authService.getUserInfo(personalAccessToken, AuthOrigin.SWAGGER);

        this.tournamentService.join(tournament.getId(), joiner);

        clock.setClock(Clock.offset(clock.getClock(), Duration.ofDays(2)));

        BattleDTO battleDTO = new BattleDTO();
        battleDTO.setTournamentId(tournament.getId());
        battleDTO.setTitle("FizzBuzz 5");
        battleDTO.setDescription("Implement FizzBuzz algorithm. Example output: 10 -> 1-2-Fizz-4-Buzz-6-7-8-Fizz-Buzz");
        battleDTO.setStartsAt(LocalDateTime.now(clock.getClock()).plusHours(1));
        battleDTO.setEndsAt(LocalDateTime.now(clock.getClock()).plusDays(7));
        battleDTO.setLanguage(BattleLanguage.GOLANG);
        battleDTO.setEnableSAT(true);
        battleDTO.setEnableManualEvaluation(true);
        battleDTO.setTimelinessBaseScore(100);

        Battle battle = this.battleService.create(battleDTO, creator);

        given()
            .contentType(ContentType.JSON)
            .headers("Authorization", "Bearer " + personalAccessToken)
            .when()
            .put("/battles/" + battle.getId() + "/join")
            .then()
            .statusCode(HttpStatus.INTERNAL_SERVER_ERROR.value());

        this.battleService.deleteById(battle.getId(), creator);
    }

    @Test
    void givenCreatedTournamentAndBattle_shouldLeaveBattle() throws OAuthException, IOException {
        TournamentDTO tournamentDTO = new TournamentDTO();
        tournamentDTO.setTitle("Tournament 1");
        tournamentDTO.setDescription("Tournament Description 1");
        tournamentDTO.setPrivacy(TournamentPrivacy.PUBLIC);
        tournamentDTO.setStartsAt(LocalDateTime.now(clock.getClock()).plusDays(1));
        tournamentDTO.setEndsAt(LocalDateTime.now(clock.getClock()).plusDays(30));
        tournamentDTO.setMaxParticipants(100);

        GHUser creator = new GHUser();
        creator.setLogin("creator1");
        Tournament tournament = this.tournamentService.create(tournamentDTO, creator);

        GHUser joiner = this.authService.getUserInfo(personalAccessToken, AuthOrigin.SWAGGER);
        GHUser joiner2 = new GHUser();
        joiner2.setLogin("joiner2");

        this.tournamentService.join(tournament.getId(), joiner);
        this.tournamentService.join(tournament.getId(), joiner2);

        clock.setClock(Clock.offset(clock.getClock(), Duration.ofDays(2)));

        BattleDTO battleDTO = new BattleDTO();
        battleDTO.setTournamentId(tournament.getId());
        battleDTO.setTitle("FizzBuzz 6");
        battleDTO.setDescription("Implement FizzBuzz algorithm. Example output: 10 -> 1-2-Fizz-4-Buzz-6-7-8-Fizz-Buzz");
        battleDTO.setStartsAt(LocalDateTime.now(clock.getClock()).plusHours(1));
        battleDTO.setEndsAt(LocalDateTime.now(clock.getClock()).plusDays(7));
        battleDTO.setLanguage(BattleLanguage.GOLANG);
        battleDTO.setEnableSAT(true);
        battleDTO.setEnableManualEvaluation(true);
        battleDTO.setTimelinessBaseScore(100);

        Battle battle = this.battleService.create(battleDTO, creator);

        try {
            this.battleService.join(battle.getId(), joiner);
            this.battleService.join(battle.getId(), joiner2);
            this.battleService.leave(battle.getId(), joiner);

            // Check that battle is still there
            assertDoesNotThrow(() -> this.battleService.findById(battle.getId()));

            // Check that joiner2 is still there
            assertThat(this.battleService.findById(battle.getId()).getParticipants().size(), equalTo(1));
        } finally {
            this.battleService.deleteBattleRepository(battle);
        }
    }

    @Test
    void givenCreatedTournamentAndBattle_shouldSubmitSolution() throws OAuthException, IOException {
        Battle battle = null;

        GHUser creator = new GHUser();
        creator.setLogin("test-creator");

        try {
            TournamentDTO tournamentDTO = new TournamentDTO();
            tournamentDTO.setTitle("Tournament 1");
            tournamentDTO.setDescription("Tournament Description 1");
            tournamentDTO.setPrivacy(TournamentPrivacy.PUBLIC);
            tournamentDTO.setStartsAt(LocalDateTime.now(clock.getClock()).plusDays(1));
            tournamentDTO.setEndsAt(LocalDateTime.now(clock.getClock()).plusDays(30));
            tournamentDTO.setMaxParticipants(100);

            Tournament tournament = this.tournamentService.create(tournamentDTO, creator);

            GHUser student = this.authService.getUserInfo(personalAccessToken, AuthOrigin.SWAGGER);
            this.tournamentService.join(tournament.getId(), student);

            clock.setClock(Clock.offset(clock.getClock(), Duration.ofDays(2)));

            BattleDTO battleDTO = new BattleDTO();
            battleDTO.setTournamentId(tournament.getId());
            battleDTO.setTitle("REAL FizzBuzz");
            battleDTO.setDescription("Implement FizzBuzz algorithm. Example output: 10 -> 1-2-Fizz-4-Buzz-6-7-8-Fizz-Buzz-");
            battleDTO.setStartsAt(LocalDateTime.now(clock.getClock()).plusHours(1));
            battleDTO.setEndsAt(LocalDateTime.now(clock.getClock()).plusDays(5));
            battleDTO.setLanguage(BattleLanguage.GOLANG);
            battleDTO.setEnableSAT(true);
            battleDTO.setEnableManualEvaluation(true);
            battleDTO.setTimelinessBaseScore(100);

            BattleTest test1 = new BattleTest();
            test1.setName("Test 1");
            test1.setPrivacy(BattleTestPrivacy.PUBLIC);
            test1.setInput("3");
            test1.setExpectedOutput("1-2-Fizz-");
            test1.setGivesScore(5);

            BattleTest test2 = new BattleTest();
            test2.setName("Test 2");
            test2.setPrivacy(BattleTestPrivacy.PUBLIC);
            test2.setInput("5");
            test2.setExpectedOutput("1-2-Fizz-4-Buzz-");
            test1.setGivesScore(5);

            BattleTest test3 = new BattleTest();
            test3.setName("Test 3");
            test3.setPrivacy(BattleTestPrivacy.PRIVATE);
            test3.setInput("15");
            test3.setExpectedOutput("1-2-Fizz-4-Buzz-Fizz-7-8-Fizz-Buzz-11-Fizz-13-14-FizzBuzz-");
            test3.setGivesScore(100);

            battleDTO.setTests(List.of(test1, test2, test3));

            battle = this.battleService.create(battleDTO, creator);

            clock.setClock(Clock.offset(clock.getClock(), Duration.ofDays(1)));

            this.battleService.join(battle.getId(), student);

            BattleEntryDTO dto = new BattleEntryDTO();
            dto.setArtifactUrl("https://transfer.sh/get/NiGc5P7RUq/fizzbuzz-kata.zip");

            GitHub github = new GitHubBuilder().withOAuthToken(personalAccessToken).build();
            BattleEntry be = this.battleService.submit(battle.getId(), dto, github);

            await().atMost(1, MINUTES)
                .untilAsserted(() -> assertThat(this.battleEntryRepository.findById(be.getId()).orElseThrow(EntityNotFoundException::new).getStatus(), equalTo(BattleEntryStatus.COMPLETED)));

            BattleEntry beAfter = this.battleEntryRepository.findById(be.getId()).orElseThrow(EntityNotFoundException::new);
            assertThat(beAfter.getStatus(), equalTo(BattleEntryStatus.COMPLETED));
        } finally {
            if (battle != null) {
                this.battleService.deleteById(battle.getId(), creator);
            }
        }
    }

}
