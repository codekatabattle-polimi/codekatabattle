package it.polimi.codekatabattle;

import it.polimi.codekatabattle.controllers.TournamentController;
import it.polimi.codekatabattle.models.dto.TournamentDTO;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;

import java.time.LocalDateTime;

import static org.springframework.http.RequestEntity.post;
import static org.springframework.test.web.client.match.MockRestRequestMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@WebMvcTest(TournamentController.class)
public class TournamentIntegrationTests {

    @Autowired
    private MockMvc mvc;

    @BeforeAll
    static void setup() {
    }

    @Test
    void shouldCreateTournament() {
        TournamentDTO dto = new TournamentDTO();
        dto.setTitle("Test Tournament");
        dto.setDescription("This is a test tournament");
        dto.setStartsAt(LocalDateTime.now());
        dto.setEndsAt(LocalDateTime.now().plusDays(1));
    }

}
