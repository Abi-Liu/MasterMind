package com.example.demo.services;

import com.example.demo.entities.Game;
import com.example.demo.models.GuessRequestDTO;
import com.example.demo.models.RuleDTO;

public interface GameService {
    Long createGame(RuleDTO ruleDTO);

    Game submitGuess(GuessRequestDTO guessRequestDTO);
}
