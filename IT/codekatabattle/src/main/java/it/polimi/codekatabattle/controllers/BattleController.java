package it.polimi.codekatabattle.controllers;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import io.swagger.v3.oas.annotations.tags.Tag;
import it.polimi.codekatabattle.entities.Battle;
import it.polimi.codekatabattle.entities.BattleEntry;
import it.polimi.codekatabattle.exceptions.OAuthException;
import it.polimi.codekatabattle.models.dto.BattleDTO;
import it.polimi.codekatabattle.models.dto.BattleEntryDTO;
import it.polimi.codekatabattle.models.dto.BattleParticipantUpdateDTO;
import it.polimi.codekatabattle.models.dto.BattleUpdateDTO;
import it.polimi.codekatabattle.models.github.GHUser;
import it.polimi.codekatabattle.services.AuthService;
import it.polimi.codekatabattle.services.BattleService;
import jakarta.persistence.EntityNotFoundException;
import jakarta.validation.Valid;
import jakarta.validation.ValidationException;
import org.apache.logging.log4j.util.Strings;
import org.kohsuke.github.GitHub;
import org.kohsuke.github.GitHubBuilder;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.io.IOException;

@Tag(name = "Battle", description = "Endpoints related to battles")
@RestController
@RequestMapping("/battles")
public class BattleController {

    private final AuthService authService;

    private final BattleService battleService;

    public BattleController(AuthService authService, BattleService battleService) {
        this.authService = authService;
        this.battleService = battleService;
    }

    @PostMapping(
        path = "",
        consumes = MediaType.APPLICATION_JSON_VALUE,
        produces = MediaType.APPLICATION_JSON_VALUE
    )
    @Operation(
        summary = "Create battle",
        description = "Create a new battle",
        security = @SecurityRequirement(name = "github")
    )
    public ResponseEntity<Battle> create(
        @RequestBody @Valid BattleDTO battle,
        @Parameter(hidden = true) @RequestHeader("Authorization") String accessToken,
        @Parameter(hidden = true) @RequestHeader(value = "Origin", required = false) String origin
    ) throws OAuthException, IOException {
        GHUser user = this.authService.getUserInfo(accessToken, this.authService.getAuthOriginFromOriginHeader(origin));
        return ResponseEntity.ok().body(this.battleService.create(battle, user));
    }

    @GetMapping(
        path = "/{id}",
        produces = MediaType.APPLICATION_JSON_VALUE
    )
    @Operation(
        summary = "Find battle by id",
        description = "Find a battle by id"
    )
    public ResponseEntity<Battle> findById(@PathVariable("id") Long id) throws EntityNotFoundException {
        return ResponseEntity.ok().body(this.battleService.findById(id)
            .orElseThrow(() -> new EntityNotFoundException("Battle not found by id " + id)));
    }

    @GetMapping(
        path = "",
        produces = MediaType.APPLICATION_JSON_VALUE
    )
    @Operation(
        summary = "Find paginated battles",
        description = "Find paginated battles by specifying page number and page size"
    )
    public ResponseEntity<Page<Battle>> findAll(
        @RequestParam(name = "page", defaultValue = "0") int page,
        @RequestParam(name = "size", defaultValue = "10") int size
    ) {
        return ResponseEntity.ok().body(this.battleService.findAll(Pageable.ofSize(size).withPage(page)));
    }

    @PutMapping(
        path = "/{id}/join",
        produces = MediaType.APPLICATION_JSON_VALUE
    )
    @Operation(
        summary = "Join a battle",
        description = "Join a battle by providing battle id and GitHub access token",
        security = @SecurityRequirement(name = "github")
    )
    public ResponseEntity<Battle> join(
        @PathVariable("id") Long id,
        @Parameter(hidden = true) @RequestHeader("Authorization") String accessToken,
        @Parameter(hidden = true) @RequestHeader(value = "Origin", required = false) String origin
    ) throws OAuthException, EntityNotFoundException {
        GHUser user = this.authService.getUserInfo(accessToken, this.authService.getAuthOriginFromOriginHeader(origin));
        return ResponseEntity.ok().body(this.battleService.join(id, user));
    }

    @PutMapping(
        path = "/{id}/leave",
        produces = MediaType.APPLICATION_JSON_VALUE
    )
    @Operation(
        summary = "Leave a battle",
        description = "Leave a battle by providing battle id and GitHub access token",
        security = @SecurityRequirement(name = "github")
    )
    public ResponseEntity<Battle> leave(
        @PathVariable("id") Long id,
        @Parameter(hidden = true) @RequestHeader("Authorization") String accessToken,
        @Parameter(hidden = true) @RequestHeader(value = "Origin", required = false) String origin
    ) throws OAuthException, EntityNotFoundException {
        GHUser user = this.authService.getUserInfo(accessToken, this.authService.getAuthOriginFromOriginHeader(origin));
        return ResponseEntity.ok().body(this.battleService.leave(id, user));
    }

    @PutMapping(
        path = "/{id}",
        consumes = MediaType.APPLICATION_JSON_VALUE,
        produces = MediaType.APPLICATION_JSON_VALUE
    )
    @Operation(
        summary = "Update battle",
        description = "Update a battle by providing battle id and new battle data",
        security = @SecurityRequirement(name = "github")
    )
    public ResponseEntity<Battle> updateById(
        @PathVariable("id") Long id,
        @Valid @RequestBody BattleUpdateDTO battle,
        @Parameter(hidden = true) @RequestHeader("Authorization") String accessToken,
        @Parameter(hidden = true) @RequestHeader(value = "Origin", required = false) String origin
    ) throws OAuthException, EntityNotFoundException {
        GHUser user = this.authService.getUserInfo(accessToken, this.authService.getAuthOriginFromOriginHeader(origin));
        return ResponseEntity.ok().body(this.battleService.updateById(id, battle, user));
    }

    @PutMapping(
        path = "/{battleId}/participant/{battleParticipantId}",
        consumes = MediaType.APPLICATION_JSON_VALUE,
        produces = MediaType.APPLICATION_JSON_VALUE
    )
    @Operation(
        summary = "Update battle",
        description = "Update a battle participant by providing battle id, participant id and new battle participant data",
        security = @SecurityRequirement(name = "github")
    )
    public ResponseEntity<Battle> updateBattleParticipantById(
        @PathVariable("battleId") Long battleId,
        @PathVariable("battleParticipantId") Long battleParticipantId,
        @Valid @RequestBody BattleParticipantUpdateDTO battleParticipant,
        @Parameter(hidden = true) @RequestHeader("Authorization") String accessToken,
        @Parameter(hidden = true) @RequestHeader(value = "Origin", required = false) String origin
    ) throws OAuthException, EntityNotFoundException {
        GHUser user = this.authService.getUserInfo(accessToken, this.authService.getAuthOriginFromOriginHeader(origin));
        return ResponseEntity.ok().body(this.battleService.updateBattleParticipantById(battleId, battleParticipantId, battleParticipant, user));
    }

    @DeleteMapping(
        path = "/{id}",
        produces = MediaType.APPLICATION_JSON_VALUE
    )
    @Operation(
        summary = "Delete battle",
        description = "Delete a battle by providing battle id",
        security = @SecurityRequirement(name = "github")
    )
    public ResponseEntity<Battle> deleteById(
        @PathVariable("id") Long id,
        @Parameter(hidden = true) @RequestHeader("Authorization") String accessToken,
        @Parameter(hidden = true) @RequestHeader(value = "Origin", required = false) String origin
    ) throws OAuthException, EntityNotFoundException, IOException {
        GHUser user = this.authService.getUserInfo(accessToken, this.authService.getAuthOriginFromOriginHeader(origin));
        return ResponseEntity.ok().body(this.battleService.deleteById(id, user));
    }

    @PostMapping(
        path = "/{id}/submit",
        consumes = MediaType.APPLICATION_JSON_VALUE,
        produces = MediaType.APPLICATION_JSON_VALUE
    )
    @Operation(
        summary = "Submit battle entry",
        description = "Submit build artifact to be tested and scored by providing battle id and build artifact URL",
        security = @SecurityRequirement(name = "github")
    )
    public ResponseEntity<BattleEntry> submit(
        @PathVariable("id") Long id,
        @Valid @RequestBody BattleEntryDTO battleEntry,
        @RequestHeader("Authorization") String personalAccessToken
    ) throws ValidationException, EntityNotFoundException, IOException {
        String githubPAT = personalAccessToken.replace("Bearer ", "");
        if (Strings.isBlank(githubPAT)) {
            throw new ValidationException("GitHub personal access token is required");
        }

        GitHub github = new GitHubBuilder().withOAuthToken(githubPAT).build();
        return ResponseEntity.ok().body(this.battleService.submit(id, battleEntry, github));
    }

}
