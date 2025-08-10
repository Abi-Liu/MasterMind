package com.example.demo.controllers;

import com.example.demo.models.GameResponseDTO;
import com.example.demo.models.GuessRequestDTO;
import com.example.demo.models.RuleDTO;
import com.example.demo.services.impl.GameServiceImpl;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;

@RestController
@RequiredArgsConstructor
@RequestMapping("/game")
public class GameController {
    private final GameServiceImpl gameService;

    // creates a new game and returns the gameId for the session
    // For now we will implement a very basic game ID that just increments for each new game created
    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    public GameResponseDTO createGame(@Valid @RequestBody RuleDTO rules) {
        return gameService.createGame(rules);
    }

    @PostMapping("/guess")
    @ResponseStatus(HttpStatus.OK)
    public GameResponseDTO submitGuess(@Valid @RequestBody GuessRequestDTO guess) {
        return gameService.submitGuess(guess);
    }


}
