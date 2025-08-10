package com.example.demo.models;

import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

@NoArgsConstructor
@Data
public class GuessRequestDTO {
    private Long gameId;
    private List<Integer> guess;
}
