package com.example.demo.services;

import com.example.demo.models.GameResponseDTO;
import com.example.demo.models.GuessRequestDTO;
import com.example.demo.models.RuleDTO;

public interface GameService {
    GameResponseDTO createGame(RuleDTO ruleDTO);

    GameResponseDTO submitGuess(GuessRequestDTO guessRequestDTO);
}
