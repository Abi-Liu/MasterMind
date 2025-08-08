package com.example.demo.entities;

import java.util.ArrayList;
import java.util.List;

/*
    This entity will be used to store all game instances and their corresponding data
 */
public class Game {
    private final long id;
    private final Rules rules;
    private final List<Integer> code;

    private GameStatus status;
    private int attempts;
    private List<GuessRecord> history;

    public Game(long id, Rules rules, List<Integer> code) {
        this.id = id;
        this.rules = rules;
        this.code = code;
        this.status = GameStatus.IN_PROGRESS;
        this.attempts = 0;
        this.history = new ArrayList<>();
    }


}
