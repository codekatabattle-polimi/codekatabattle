package it.polimi.codekatabattle.services.impl;

import it.polimi.codekatabattle.entities.Tournament;
import it.polimi.codekatabattle.models.dto.TournamentDTO;
import it.polimi.codekatabattle.repositories.TournamentRepository;
import it.polimi.codekatabattle.services.TournamentService;
import jakarta.validation.Valid;
import jakarta.validation.ValidationException;
import jakarta.validation.constraints.NotNull;

public class TournamentServiceImpl extends CrudServiceImpl<Tournament> implements TournamentService {

    private final TournamentRepository tournamentRepository;

    public TournamentServiceImpl(TournamentRepository tournamentRepository) {
        super(tournamentRepository);
        this.tournamentRepository = tournamentRepository;
    }

    public Tournament create(@Valid @NotNull TournamentDTO tournament) throws ValidationException {
        // TODO: 27/01/2024 Implement this method
        return null;
    }

}
