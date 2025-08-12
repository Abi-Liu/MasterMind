package com.example.demo.models;

import com.example.demo.entities.GameStatus;
import com.example.demo.entities.GuessRecord;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

@NoArgsConstructor
@AllArgsConstructor
@Data
public class GameResponseDTO {
    private long id;
    private RuleDTO rules;
    private GameStatus status;
    private int attempts;
    private List<GuessRecord> history;

    private int maxHints;
    private int hintsUsed;
    private List<HintResponseDTO> hints;


    // overloaded constructor to not break existing tests
    public GameResponseDTO(long id, RuleDTO rules, GameStatus status, int attempts, List<GuessRecord> history) {
        this(id, rules, status, attempts, history, 1, 0, List.of());
    }
}
