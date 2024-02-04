package it.polimi.codekatabattle.integration;

import io.restassured.http.ContentType;
import it.polimi.codekatabattle.entities.*;
import it.polimi.codekatabattle.exceptions.OAuthException;
import it.polimi.codekatabattle.models.BattleTest;
import it.polimi.codekatabattle.models.dto.BattleDTO;
import it.polimi.codekatabattle.models.dto.TournamentDTO;
import it.polimi.codekatabattle.models.github.GHUser;
import it.polimi.codekatabattle.models.oauth.AuthOrigin;
import it.polimi.codekatabattle.repositories.BattleRepository;
import it.polimi.codekatabattle.services.AuthService;
import it.polimi.codekatabattle.services.BattleService;
import it.polimi.codekatabattle.services.TournamentService;
import org.junit.jupiter.api.Test;
import org.kohsuke.github.GHRepository;
import org.kohsuke.github.GitHub;
import org.kohsuke.github.GitHubBuilder;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.HttpStatus;

import java.io.IOException;
import java.time.LocalDateTime;
import java.util.List;

import static io.restassured.RestAssured.given;
import static org.hamcrest.CoreMatchers.equalTo;
import static org.hamcrest.MatcherAssert.assertThat;
import static org.junit.jupiter.api.Assertions.assertNotNull;

@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
public class BattleIntegrationTests extends BaseIntegrationTestSetup {

    @Autowired
    private AuthService authService;

    @Autowired
    private TournamentService tournamentService;

    @Autowired
    private BattleService battleService;

    public BattleIntegrationTests(@Autowired BattleRepository tournamentRepository) {
        super(tournamentRepository);
    }

    @Test
    void givenCreatedTournament_shouldCreateBattle() throws OAuthException, IOException {
        TournamentDTO tournamentDTO = new TournamentDTO();
        tournamentDTO.setTitle("Tournament 1");
        tournamentDTO.setDescription("Tournament Description 1");
        tournamentDTO.setPrivacy(TournamentPrivacy.PUBLIC);
        tournamentDTO.setStartsAt(LocalDateTime.now().plusHours(1));
        tournamentDTO.setEndsAt(LocalDateTime.now().plusDays(5));
        tournamentDTO.setMaxParticipants(100);

        GHUser user = this.authService.getUserInfo(personalAccessToken, AuthOrigin.SWAGGER);
        Tournament tournament = this.tournamentService.create(tournamentDTO, user);

        BattleDTO battleDTO = new BattleDTO();
        battleDTO.setTournamentId(tournament.getId());
        battleDTO.setTitle("FizzBuzz");
        battleDTO.setDescription("Implement FizzBuzz algorithm. Example output: 10 -> 1-2-Fizz-4-Buzz-6-7-8-Fizz-Buzz");
        battleDTO.setStartsAt(LocalDateTime.now().plusHours(1));
        battleDTO.setEndsAt(LocalDateTime.now().plusDays(5));
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
        tournamentDTO.setStartsAt(LocalDateTime.now().plusHours(1));
        tournamentDTO.setEndsAt(LocalDateTime.now().plusDays(5));
        tournamentDTO.setMaxParticipants(100);

        GHUser user = this.authService.getUserInfo(personalAccessToken, AuthOrigin.SWAGGER);
        Tournament tournament = this.tournamentService.create(tournamentDTO, user);

        BattleDTO battleDTO = new BattleDTO();
        battleDTO.setTournamentId(tournament.getId());
        battleDTO.setTitle("FizzBuzz 2");
        battleDTO.setDescription("Implement FizzBuzz algorithm. Example output: 10 -> 1-2-Fizz-4-Buzz-6-7-8-Fizz-Buzz");
        battleDTO.setStartsAt(LocalDateTime.now().plusHours(1));
        battleDTO.setEndsAt(LocalDateTime.now().plusDays(5));
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
        tournamentDTO.setStartsAt(LocalDateTime.now().plusHours(1));
        tournamentDTO.setEndsAt(LocalDateTime.now().plusDays(5));
        tournamentDTO.setMaxParticipants(100);

        GHUser user = this.authService.getUserInfo(personalAccessToken, AuthOrigin.SWAGGER);
        Tournament tournament = this.tournamentService.create(tournamentDTO, user);

        BattleDTO battleDTO = new BattleDTO();
        battleDTO.setTournamentId(tournament.getId());
        battleDTO.setTitle("FizzBuzz 3");
        battleDTO.setDescription("Implement FizzBuzz algorithm. Example output: 10 -> 1-2-Fizz-4-Buzz-6-7-8-Fizz-Buzz");
        battleDTO.setStartsAt(LocalDateTime.now().plusHours(1));
        battleDTO.setEndsAt(LocalDateTime.now().plusDays(5));
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
        tournamentDTO.setStartsAt(LocalDateTime.now().plusHours(1));
        tournamentDTO.setEndsAt(LocalDateTime.now().plusDays(5));
        tournamentDTO.setMaxParticipants(100);

        GHUser creator = new GHUser();
        creator.setLogin("creator1");
        Tournament tournament = this.tournamentService.create(tournamentDTO, creator);

        GHUser joiner = this.authService.getUserInfo(personalAccessToken, AuthOrigin.SWAGGER);

        this.tournamentService.join(tournament.getId(), joiner);

        BattleDTO battleDTO = new BattleDTO();
        battleDTO.setTournamentId(tournament.getId());
        battleDTO.setTitle("FizzBuzz 4");
        battleDTO.setDescription("Implement FizzBuzz algorithm. Example output: 10 -> 1-2-Fizz-4-Buzz-6-7-8-Fizz-Buzz");
        battleDTO.setStartsAt(LocalDateTime.now().minusHours(1));
        battleDTO.setEndsAt(LocalDateTime.now().plusDays(5));
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
        tournamentDTO.setStartsAt(LocalDateTime.now().plusHours(1));
        tournamentDTO.setEndsAt(LocalDateTime.now().plusDays(5));
        tournamentDTO.setMaxParticipants(100);

        GHUser creator = new GHUser();
        creator.setLogin("creator1");
        Tournament tournament = this.tournamentService.create(tournamentDTO, creator);

        GHUser joiner = this.authService.getUserInfo(personalAccessToken, AuthOrigin.SWAGGER);

        this.tournamentService.join(tournament.getId(), joiner);

        BattleDTO battleDTO = new BattleDTO();
        battleDTO.setTournamentId(tournament.getId());
        battleDTO.setTitle("FizzBuzz 5");
        battleDTO.setDescription("Implement FizzBuzz algorithm. Example output: 10 -> 1-2-Fizz-4-Buzz-6-7-8-Fizz-Buzz");
        battleDTO.setStartsAt(LocalDateTime.now().plusHours(1));
        battleDTO.setEndsAt(LocalDateTime.now().plusDays(5));
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

}
