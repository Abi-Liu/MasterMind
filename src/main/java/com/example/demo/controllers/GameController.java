package com.example.demo.controllers;

import com.example.demo.models.RuleDTO;
import com.example.demo.services.GameService;
import com.example.demo.services.RandomNumberService;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;
import java.util.concurrent.atomic.AtomicLong;

@RestController
@RequiredArgsConstructor
@RequestMapping("/game")
public class GameController {
    private final GameService gameService;

    // creates a new game and returns the gameId for the session
    // For now we will implement a very basic game ID that just increments for each new game created
    @PostMapping
    public Long createGame(RuleDTO rules) {
        return gameService.createGame(rules);
    }

}
