package com.example.demo.utils;

import com.example.demo.entities.Game;
import com.example.demo.entities.Rules;

import java.util.List;

public class GameTestBuilder {

    public static Game createGame(Long id, List<Integer> code) {
        Rules rules = new Rules(4, 7, 10);
        Game game = new Game(id, rules, code);
        return game;
    }
}
