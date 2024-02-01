package it.polimi.codekatabattle;

import static io.restassured.RestAssured.given;
import static org.hamcrest.CoreMatchers.equalTo;

import io.restassured.http.ContentType;
import it.polimi.codekatabattle.entities.Tournament;
import it.polimi.codekatabattle.entities.TournamentPrivacy;
import it.polimi.codekatabattle.exceptions.OAuthException;
import it.polimi.codekatabattle.models.dto.TournamentDTO;
import it.polimi.codekatabattle.models.github.GHUser;
import it.polimi.codekatabattle.models.oauth.AuthOrigin;
import it.polimi.codekatabattle.repositories.TournamentRepository;
import it.polimi.codekatabattle.services.AuthService;
import it.polimi.codekatabattle.services.TournamentService;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.HttpStatus;

import java.time.LocalDateTime;
import java.util.List;

@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
public class TournamentIntegrationTests extends BaseIntegrationTestSetup {

    @Autowired
    private AuthService authService;

    @Autowired
    private TournamentService tournamentService;

    public TournamentIntegrationTests(@Autowired TournamentRepository tournamentRepository) {
        super(tournamentRepository);
    }

    @Test
    void shouldCreateTournament() {
        TournamentDTO tournamentDTO = new TournamentDTO();
        tournamentDTO.setTitle("Tournament 1");
        tournamentDTO.setDescription("Tournament Description 1");
        tournamentDTO.setPrivacy(TournamentPrivacy.PUBLIC);
        tournamentDTO.setStartsAt(LocalDateTime.now().plusHours(1));
        tournamentDTO.setEndsAt(LocalDateTime.now().plusDays(5));
        tournamentDTO.setMaxParticipants(100);

        given()
            .contentType(ContentType.JSON)
            .headers("Authorization", "Bearer " + personalAccessToken)
            .body(tournamentDTO)
            .when()
            .post("/tournaments")
            .then()
            .statusCode(HttpStatus.OK.value())
            .body("title", equalTo(tournamentDTO.getTitle()));
    }

    @Test
    void givenCreatedTournament_shouldGetById() throws OAuthException {
        TournamentDTO tournamentDTO = new TournamentDTO();
        tournamentDTO.setTitle("Tournament 1");
        tournamentDTO.setDescription("Tournament Description 1");
        tournamentDTO.setPrivacy(TournamentPrivacy.PUBLIC);
        tournamentDTO.setStartsAt(LocalDateTime.now().plusHours(1));
        tournamentDTO.setEndsAt(LocalDateTime.now().plusDays(5));
        tournamentDTO.setMaxParticipants(100);

        GHUser user = this.authService.getUserInfo(personalAccessToken, AuthOrigin.SWAGGER);
        Tournament tournament = this.tournamentService.create(tournamentDTO, user);

        given()
            .contentType(ContentType.JSON)
            .headers("Authorization", "Bearer " + personalAccessToken)
            .body(tournamentDTO)
            .when()
            .get("/tournaments/" + tournament.getId())
            .then()
            .statusCode(HttpStatus.OK.value())
            .body("title", equalTo(tournamentDTO.getTitle()));
    }

    @Test
    void givenCreatedTournaments_shouldGetPageNotIncludingPrivates() throws OAuthException {
        TournamentDTO tournamentDTO1 = new TournamentDTO();
        tournamentDTO1.setTitle("Tournament 1");
        tournamentDTO1.setDescription("Tournament Description 1");
        tournamentDTO1.setPrivacy(TournamentPrivacy.PUBLIC);
        tournamentDTO1.setStartsAt(LocalDateTime.now().plusHours(1));
        tournamentDTO1.setEndsAt(LocalDateTime.now().plusDays(5));
        tournamentDTO1.setMaxParticipants(100);

        TournamentDTO tournamentDTO2 = new TournamentDTO();
        tournamentDTO2.setTitle("Tournament 2");
        tournamentDTO2.setDescription("Tournament Description 2");
        tournamentDTO2.setPrivacy(TournamentPrivacy.PUBLIC);
        tournamentDTO2.setStartsAt(LocalDateTime.now().plusHours(1));
        tournamentDTO2.setEndsAt(LocalDateTime.now().plusDays(5));
        tournamentDTO2.setMaxParticipants(200);

        TournamentDTO tournamentDTO3 = new TournamentDTO();
        tournamentDTO3.setTitle("Tournament 3");
        tournamentDTO3.setDescription("Tournament Description 3");
        tournamentDTO3.setPrivacy(TournamentPrivacy.PRIVATE);
        tournamentDTO3.setStartsAt(LocalDateTime.now().plusHours(1));
        tournamentDTO3.setEndsAt(LocalDateTime.now().plusDays(5));
        tournamentDTO3.setMaxParticipants(300);

        GHUser user = this.authService.getUserInfo(personalAccessToken, AuthOrigin.SWAGGER);
        Tournament tournament1 = this.tournamentService.create(tournamentDTO1, user);
        Tournament tournament2 = this.tournamentService.create(tournamentDTO2, user);
        this.tournamentService.create(tournamentDTO3, user);

        given()
            .contentType(ContentType.JSON)
            .when()
            .get("/tournaments?page=0&size=10")
            .then()
            .statusCode(HttpStatus.OK.value())
            .body("content.size()", equalTo(2))
            .body("content[0].id", equalTo(tournament1.getId().intValue()))
            .body("content[1].id", equalTo(tournament2.getId().intValue()));
    }

    @Test
    void givenCreatedTournaments_shouldGetPageOfTournamentsCreatedByUser() {
        TournamentDTO tournamentDTO1 = new TournamentDTO();
        tournamentDTO1.setTitle("Tournament 1");
        tournamentDTO1.setDescription("Tournament Description 1");
        tournamentDTO1.setPrivacy(TournamentPrivacy.PUBLIC);
        tournamentDTO1.setStartsAt(LocalDateTime.now().plusHours(1));
        tournamentDTO1.setEndsAt(LocalDateTime.now().plusDays(5));
        tournamentDTO1.setMaxParticipants(100);

        TournamentDTO tournamentDTO2 = new TournamentDTO();
        tournamentDTO2.setTitle("Tournament 2");
        tournamentDTO2.setDescription("Tournament Description 2");
        tournamentDTO2.setPrivacy(TournamentPrivacy.PUBLIC);
        tournamentDTO2.setStartsAt(LocalDateTime.now().plusHours(1));
        tournamentDTO2.setEndsAt(LocalDateTime.now().plusDays(5));
        tournamentDTO2.setMaxParticipants(200);

        GHUser creator1 = new GHUser();
        creator1.setLogin("creator1");
        GHUser creator2 = new GHUser();
        creator2.setLogin("creator2");

        Tournament tournament = this.tournamentService.create(tournamentDTO1, creator1);
        this.tournamentService.create(tournamentDTO2, creator2);

        given()
            .contentType(ContentType.JSON)
            .headers("Authorization", "Bearer " + personalAccessToken)
            .when()
            .get("/tournaments/created?page=0&size=10&creator=" + creator1.getLogin())
            .then()
            .statusCode(HttpStatus.OK.value())
            .body("content.size()", equalTo(1))
            .body("content[0].id", equalTo(tournament.getId().intValue()));
    }

    @Test
    void givenCreatedTournaments_shouldGetPageOfTournamentsJoinedByUser() throws OAuthException {
        TournamentDTO tournamentDTO1 = new TournamentDTO();
        tournamentDTO1.setTitle("Tournament 1");
        tournamentDTO1.setDescription("Tournament Description 1");
        tournamentDTO1.setPrivacy(TournamentPrivacy.PUBLIC);
        tournamentDTO1.setStartsAt(LocalDateTime.now().plusHours(1));
        tournamentDTO1.setEndsAt(LocalDateTime.now().plusDays(5));
        tournamentDTO1.setMaxParticipants(100);

        TournamentDTO tournamentDTO2 = new TournamentDTO();
        tournamentDTO2.setTitle("Tournament 2");
        tournamentDTO2.setDescription("Tournament Description 2");
        tournamentDTO2.setPrivacy(TournamentPrivacy.PUBLIC);
        tournamentDTO2.setStartsAt(LocalDateTime.now().plusHours(1));
        tournamentDTO2.setEndsAt(LocalDateTime.now().plusDays(5));
        tournamentDTO2.setMaxParticipants(200);

        GHUser creator = new GHUser();
        creator.setLogin("creator");
        this.tournamentService.create(tournamentDTO1, creator);
        Tournament tournament2 = this.tournamentService.create(tournamentDTO2, creator);

        GHUser joiner = this.authService.getUserInfo(personalAccessToken, AuthOrigin.SWAGGER);
        this.tournamentService.join(tournament2.getId(), joiner);

        given()
            .contentType(ContentType.JSON)
            .headers("Authorization", "Bearer " + personalAccessToken)
            .when()
            .get("/tournaments/joined?page=0&size=10&participant=" + joiner.getLogin())
            .then()
            .statusCode(HttpStatus.OK.value())
            .body("content.size()", equalTo(1))
            .body("content[0].id", equalTo(tournament2.getId().intValue()));
    }

    @Test
    void givenCreatedTournaments_shouldGetPageOfTournamentsCoordinatedByUser() {
        TournamentDTO tournamentDTO1 = new TournamentDTO();
        tournamentDTO1.setTitle("Tournament 1");
        tournamentDTO1.setDescription("Tournament Description 1");
        tournamentDTO1.setPrivacy(TournamentPrivacy.PUBLIC);
        tournamentDTO1.setStartsAt(LocalDateTime.now().plusHours(1));
        tournamentDTO1.setEndsAt(LocalDateTime.now().plusDays(5));
        tournamentDTO1.setMaxParticipants(100);
        tournamentDTO1.setCoordinators(List.of("coordinator1", "coordinator2"));

        TournamentDTO tournamentDTO2 = new TournamentDTO();
        tournamentDTO2.setTitle("Tournament 2");
        tournamentDTO2.setDescription("Tournament Description 2");
        tournamentDTO2.setPrivacy(TournamentPrivacy.PUBLIC);
        tournamentDTO2.setStartsAt(LocalDateTime.now().plusHours(1));
        tournamentDTO2.setEndsAt(LocalDateTime.now().plusDays(5));
        tournamentDTO2.setMaxParticipants(200);

        GHUser creator1 = new GHUser();
        creator1.setLogin("creator1");
        GHUser creator2 = new GHUser();
        creator2.setLogin("creator2");

        Tournament tournament = this.tournamentService.create(tournamentDTO1, creator1);
        this.tournamentService.create(tournamentDTO2, creator2);

        given()
            .contentType(ContentType.JSON)
            .headers("Authorization", "Bearer " + personalAccessToken)
            .when()
            .get("/tournaments/coordinated?page=0&size=10&coordinator=coordinator1")
            .then()
            .statusCode(HttpStatus.OK.value())
            .body("content.size()", equalTo(1))
            .body("content[0].id", equalTo(tournament.getId().intValue()));
    }

    @Test
    void givenCreatedTournament_shouldJoinTournament() throws OAuthException {
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

        given()
            .contentType(ContentType.JSON)
            .headers("Authorization", "Bearer " + personalAccessToken)
            .when()
            .put("/tournaments/" + tournament.getId() + "/join")
            .then()
            .statusCode(HttpStatus.OK.value())
            .body("participants.size()", equalTo(1))
            .body("participants[0].username", equalTo(joiner.getLogin()));
    }

    @Test
    void givenCreatedTournaments_shouldBlockCreatorFromJoiningTournament() throws OAuthException {
        TournamentDTO tournamentDTO = new TournamentDTO();
        tournamentDTO.setTitle("Tournament 1");
        tournamentDTO.setDescription("Tournament Description 1");
        tournamentDTO.setPrivacy(TournamentPrivacy.PUBLIC);
        tournamentDTO.setStartsAt(LocalDateTime.now().plusHours(1));
        tournamentDTO.setEndsAt(LocalDateTime.now().plusDays(5));
        tournamentDTO.setMaxParticipants(100);

        GHUser creator = this.authService.getUserInfo(personalAccessToken, AuthOrigin.SWAGGER);
        Tournament tournament = this.tournamentService.create(tournamentDTO, creator);

        given()
            .contentType(ContentType.JSON)
            .headers("Authorization", "Bearer " + personalAccessToken)
            .when()
            .put("/tournaments/" + tournament.getId() + "/join")
            .then()
            .statusCode(HttpStatus.INTERNAL_SERVER_ERROR.value());
    }

    @Test
    void givenCreatedTournament_shouldLeaveTournament() throws OAuthException {
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

        given()
            .contentType(ContentType.JSON)
            .headers("Authorization", "Bearer " + personalAccessToken)
            .when()
            .put("/tournaments/" + tournament.getId() + "/join")
            .then()
            .statusCode(HttpStatus.OK.value())
            .body("participants.size()", equalTo(1))
            .body("participants[0].username", equalTo(joiner.getLogin()));

        given()
            .contentType(ContentType.JSON)
            .headers("Authorization", "Bearer " + personalAccessToken)
            .when()
            .put("/tournaments/" + tournament.getId() + "/leave")
            .then()
            .statusCode(HttpStatus.OK.value())
            .body("participants.size()", equalTo(0));
    }

    @Test
    void givenCreatedTournament_shouldUpdateTournament() throws OAuthException {
        TournamentDTO tournamentDTO = new TournamentDTO();
        tournamentDTO.setTitle("Tournament 1");
        tournamentDTO.setDescription("Tournament Description 1");
        tournamentDTO.setPrivacy(TournamentPrivacy.PUBLIC);
        tournamentDTO.setStartsAt(LocalDateTime.now().plusHours(1));
        tournamentDTO.setEndsAt(LocalDateTime.now().plusDays(5));
        tournamentDTO.setMaxParticipants(100);

        GHUser creator = this.authService.getUserInfo(personalAccessToken, AuthOrigin.SWAGGER);
        Tournament tournament = this.tournamentService.create(tournamentDTO, creator);

        TournamentDTO updateDTO = new TournamentDTO();
        updateDTO.setTitle("Updated Tournament 1");
        updateDTO.setDescription("Updated Tournament Description 1");
        updateDTO.setPrivacy(TournamentPrivacy.PRIVATE);
        updateDTO.setStartsAt(LocalDateTime.now().plusHours(2));
        updateDTO.setEndsAt(LocalDateTime.now().plusDays(6));
        updateDTO.setMaxParticipants(200);

        given()
            .contentType(ContentType.JSON)
            .headers("Authorization", "Bearer " + personalAccessToken)
            .body(updateDTO)
            .when()
            .put("/tournaments/" + tournament.getId())
            .then()
            .statusCode(HttpStatus.OK.value())
            .body("title", equalTo(updateDTO.getTitle()))
            .body("description", equalTo(updateDTO.getDescription()))
            .body("privacy", equalTo(updateDTO.getPrivacy().toString()))
            .body("maxParticipants", equalTo(updateDTO.getMaxParticipants()));
    }

    @Test
    void givenCreatedTournament_shouldDeleteTournament() throws OAuthException {
        TournamentDTO tournamentDTO = new TournamentDTO();
        tournamentDTO.setTitle("Tournament 1");
        tournamentDTO.setDescription("Tournament Description 1");
        tournamentDTO.setPrivacy(TournamentPrivacy.PUBLIC);
        tournamentDTO.setStartsAt(LocalDateTime.now().plusHours(1));
        tournamentDTO.setEndsAt(LocalDateTime.now().plusDays(5));
        tournamentDTO.setMaxParticipants(100);

        GHUser creator = this.authService.getUserInfo(personalAccessToken, AuthOrigin.SWAGGER);
        Tournament tournament = this.tournamentService.create(tournamentDTO, creator);

        given()
            .contentType(ContentType.JSON)
            .headers("Authorization", "Bearer " + personalAccessToken)
            .when()
            .delete("/tournaments/" + tournament.getId())
            .then()
            .statusCode(HttpStatus.OK.value());
    }

    @Test
    void givenCreatedTournament_shouldBlockUpdateAndDeleteToNonCreator() {
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

        TournamentDTO updateDTO = new TournamentDTO();
        updateDTO.setTitle("Updated Tournament 1");
        updateDTO.setDescription("Updated Tournament Description 1");
        updateDTO.setPrivacy(TournamentPrivacy.PRIVATE);
        updateDTO.setStartsAt(LocalDateTime.now().plusHours(2));
        updateDTO.setEndsAt(LocalDateTime.now().plusDays(6));
        updateDTO.setMaxParticipants(200);

        given()
            .contentType(ContentType.JSON)
            .headers("Authorization", "Bearer " + personalAccessToken)
            .body(updateDTO)
            .when()
            .put("/tournaments/" + tournament.getId())
            .then()
            .statusCode(HttpStatus.INTERNAL_SERVER_ERROR.value());

        given()
            .contentType(ContentType.JSON)
            .headers("Authorization", "Bearer " + personalAccessToken)
            .when()
            .delete("/tournaments/" + tournament.getId())
            .then()
            .statusCode(HttpStatus.INTERNAL_SERVER_ERROR.value());
    }

}
