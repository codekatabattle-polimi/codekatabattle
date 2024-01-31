package it.polimi.codekatabattle.controllers;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import io.swagger.v3.oas.annotations.tags.Tag;
import it.polimi.codekatabattle.entities.Tournament;
import it.polimi.codekatabattle.exceptions.OAuthException;
import it.polimi.codekatabattle.models.dto.TournamentDTO;
import it.polimi.codekatabattle.models.github.GHUser;
import it.polimi.codekatabattle.services.AuthService;
import it.polimi.codekatabattle.services.TournamentService;
import jakarta.persistence.EntityNotFoundException;
import jakarta.validation.Valid;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@Tag(name = "Tournament", description = "Endpoints related to tournaments")
@RestController
@RequestMapping("/tournaments")
public class TournamentController {

    private final AuthService authService;

    private final TournamentService tournamentService;

    public TournamentController(AuthService authService, TournamentService tournamentService) {
        this.authService = authService;
        this.tournamentService = tournamentService;
    }

    @PostMapping(
        path = "",
        consumes = MediaType.APPLICATION_JSON_VALUE,
        produces = MediaType.APPLICATION_JSON_VALUE
    )
    @Operation(
        summary = "Create tournament",
        description = "Create a new tournament",
        security = @SecurityRequirement(name = "github")
    )
    public ResponseEntity<Tournament> create(
        @RequestBody @Valid TournamentDTO tournament,
        @Parameter(hidden = true) @RequestHeader("Authorization") String accessToken,
        @Parameter(hidden = true) @RequestHeader(value = "Origin", required = false) String origin
    ) throws OAuthException {
        GHUser user = this.authService.getUserInfo(accessToken, this.authService.getAuthOriginFromOriginHeader(origin));
        return ResponseEntity.ok().body(this.tournamentService.create(tournament, user));
    }

    @GetMapping(
        path = "/{id}",
        produces = MediaType.APPLICATION_JSON_VALUE
    )
    @Operation(
        summary = "Find tournament by id",
        description = "Find a tournament by id"
    )
    public ResponseEntity<Tournament> findById(@PathVariable("id") Long id) throws EntityNotFoundException {
        return ResponseEntity.ok().body(this.tournamentService.findById(id)
            .orElseThrow(() -> new EntityNotFoundException("Tournament not found by id " + id)));
    }

    @GetMapping(
        path = "",
        produces = MediaType.APPLICATION_JSON_VALUE
    )
    @Operation(
        summary = "Find paginated tournaments",
        description = "Find paginated tournaments by specifying page number and page size"
    )
    public ResponseEntity<Page<Tournament>> findAll(
        @RequestParam(name = "page", defaultValue = "0") int page,
        @RequestParam(name = "size", defaultValue = "10") int size
    ) {
        return ResponseEntity.ok().body(this.tournamentService.findAll(Pageable.ofSize(size).withPage(page)));
    }

    @GetMapping(
        path = "/created",
        produces = MediaType.APPLICATION_JSON_VALUE
    )
    @Operation(
        summary = "Find paginated tournaments created by user",
        description = "Find paginated tournaments created by user by specifying page number and page size"
    )
    public ResponseEntity<Page<Tournament>> findAllCreatedByUser(
        @RequestParam(name = "page", defaultValue = "0") int page,
        @RequestParam(name = "size", defaultValue = "10") int size,
        @RequestParam(name = "creator") String creator
    ) {
        return ResponseEntity.ok().body(this.tournamentService.findAllByCreator(Pageable.ofSize(size).withPage(page), creator));
    }

    @GetMapping(
        path = "/joined",
        produces = MediaType.APPLICATION_JSON_VALUE
    )
    @Operation(
        summary = "Find paginated tournaments joined by user",
        description = "Find paginated tournaments joined by user by specifying page number and page size"
    )
    public ResponseEntity<Page<Tournament>> findAllJoinedByUser(
        @RequestParam(name = "page", defaultValue = "0") int page,
        @RequestParam(name = "size", defaultValue = "10") int size,
        @RequestParam(name = "participant") String participant
    ) {
        return ResponseEntity.ok().body(this.tournamentService.findAllByParticipant(Pageable.ofSize(size).withPage(page), participant));
    }

    @GetMapping(
        path = "/coordinated",
        produces = MediaType.APPLICATION_JSON_VALUE
    )
    @Operation(
        summary = "Find paginated tournaments coordinated by user",
        description = "Find paginated tournaments coordinated by user by specifying page number and page size"
    )
    public ResponseEntity<Page<Tournament>> findAllCoordinatedByUser(
        @RequestParam(name = "page", defaultValue = "0") int page,
        @RequestParam(name = "size", defaultValue = "10") int size,
        @RequestParam(name = "coordinator") String coordinator
    ) {
        return ResponseEntity.ok().body(this.tournamentService.findAllByCoordinator(Pageable.ofSize(size).withPage(page), coordinator));
    }

    @PutMapping(
        path = "/{id}/join",
        produces = MediaType.APPLICATION_JSON_VALUE
    )
    @Operation(
        summary = "Join a tournament",
        description = "Join a tournament by providing tournament id and GitHub access token",
        security = @SecurityRequirement(name = "github")
    )
    public ResponseEntity<Tournament> join(
        @PathVariable("id") Long id,
        @Parameter(hidden = true) @RequestHeader("Authorization") String accessToken,
        @Parameter(hidden = true) @RequestHeader(value = "Origin", required = false) String origin
    ) throws OAuthException, EntityNotFoundException {
        GHUser user = this.authService.getUserInfo(accessToken, this.authService.getAuthOriginFromOriginHeader(origin));
        return ResponseEntity.ok().body(this.tournamentService.join(id, user));
    }

    @PutMapping(
        path = "/{id}/leave",
        produces = MediaType.APPLICATION_JSON_VALUE
    )
    @Operation(
        summary = "Leave a tournament",
        description = "Leave a tournament by providing tournament id and GitHub access token",
        security = @SecurityRequirement(name = "github")
    )
    public ResponseEntity<Tournament> leave(
        @PathVariable("id") Long id,
        @Parameter(hidden = true) @RequestHeader("Authorization") String accessToken,
        @Parameter(hidden = true) @RequestHeader(value = "Origin", required = false) String origin
    ) throws OAuthException, EntityNotFoundException {
        GHUser user = this.authService.getUserInfo(accessToken, this.authService.getAuthOriginFromOriginHeader(origin));
        return ResponseEntity.ok().body(this.tournamentService.leave(id, user));
    }

    @PutMapping(
        path = "/{id}",
        consumes = MediaType.APPLICATION_JSON_VALUE,
        produces = MediaType.APPLICATION_JSON_VALUE
    )
    @Operation(
        summary = "Update tournament",
        description = "Update a tournament by providing tournament id and new tournament data",
        security = @SecurityRequirement(name = "github")
    )
    public ResponseEntity<Tournament> updateById(
        @PathVariable("id") Long id,
        @Valid @RequestBody TournamentDTO tournament,
        @Parameter(hidden = true) @RequestHeader("Authorization") String accessToken,
        @Parameter(hidden = true) @RequestHeader(value = "Origin", required = false) String origin
    ) throws OAuthException, EntityNotFoundException {
        GHUser user = this.authService.getUserInfo(accessToken, this.authService.getAuthOriginFromOriginHeader(origin));
        return ResponseEntity.ok().body(this.tournamentService.updateById(id, tournament, user));
    }

    @DeleteMapping(
        path = "/{id}",
        produces = MediaType.APPLICATION_JSON_VALUE
    )
    @Operation(
        summary = "Delete tournament",
        description = "Delete a tournament by providing tournament id",
        security = @SecurityRequirement(name = "github")
    )
    public ResponseEntity<Tournament> deleteById(
        @PathVariable("id") Long id,
        @Parameter(hidden = true) @RequestHeader("Authorization") String accessToken,
        @Parameter(hidden = true) @RequestHeader(value = "Origin", required = false) String origin
    ) throws OAuthException, EntityNotFoundException {
        GHUser user = this.authService.getUserInfo(accessToken, this.authService.getAuthOriginFromOriginHeader(origin));
        return ResponseEntity.ok().body(this.tournamentService.deleteById(id, user));
    }

}
