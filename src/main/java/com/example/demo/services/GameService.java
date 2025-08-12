package com.example.demo.services;

import com.example.demo.entities.Game;
import com.example.demo.models.GameResponseDTO;
import com.example.demo.models.GuessRequestDTO;
import com.example.demo.models.RuleDTO;

public interface GameService {
    Game findGameById(Long id);
    GameResponseDTO createGame(RuleDTO ruleDTO);

    GameResponseDTO submitGuess(GuessRequestDTO guessRequestDTO);

    boolean isGameInProgress(Game game);
}
