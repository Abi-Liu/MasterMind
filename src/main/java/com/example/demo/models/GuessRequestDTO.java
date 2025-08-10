package com.example.demo.models;

import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

@NoArgsConstructor
@AllArgsConstructor
@Data
public class GuessRequestDTO {
    @NotNull
    private Long gameId;

    @NotNull
    private List<Integer> guess;
}
