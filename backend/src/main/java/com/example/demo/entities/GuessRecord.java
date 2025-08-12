package com.example.demo.entities;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

@Data
@NoArgsConstructor
@AllArgsConstructor
/*
    Stores each guess and its corresponding result
 */
public class GuessRecord {
    private List<Integer> guess;
    private GuessResult result;
}
