package com.example.demo.entities;

import com.example.demo.models.HintResponseDTO;
import lombok.Data;

import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

/*
    This entity will be used to store all game instances and their corresponding data
 */
@Data
public class Game {
    private final long id;
    private final Rules rules;
    private final List<Integer> code;

    private GameStatus status;
    private int attempts;
    private List<GuessRecord> history;
    private int hintsUsed;
    private int maxHints;
    private Map<Integer, Integer> hints;

    public Game(long id, Rules rules, List<Integer> code) {
        // constructor overload to not break existing tests. defaults maxHints to 1.
        this(id, rules, code, 1);
    }

    public Game(long id, Rules rules, List<Integer> code, int maxHints) {
        this.id = id;
        this.rules = rules;
        this.code = code;
        this.status = GameStatus.IN_PROGRESS;
        this.attempts = 0;
        this.history = new ArrayList<>();

        this.maxHints = maxHints;
        this.hintsUsed = 0;
        // i'm using linked hashmaps here in order to preserve the ordering in which the hints are used.
        this.hints = new LinkedHashMap<>();
    }



}
