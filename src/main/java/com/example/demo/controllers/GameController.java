package com.example.demo.controllers;

import com.example.demo.models.RuleDTO;
import com.example.demo.services.impl.GameServiceImpl;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequiredArgsConstructor
@RequestMapping("/game")
public class GameController {
    private final GameServiceImpl gameService;

    // creates a new game and returns the gameId for the session
    // For now we will implement a very basic game ID that just increments for each new game created
    @PostMapping
    public Long createGame(RuleDTO rules) {
        return gameService.createGame(rules);
    }

}
