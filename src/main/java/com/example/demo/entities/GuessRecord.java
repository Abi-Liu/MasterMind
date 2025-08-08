package com.example.demo.entities;

import lombok.Data;

import java.util.List;

@Data
/*
    Stores each guess and its corresponding result
 */
public class GuessRecord {
    private List<Integer> guess;
    private GuessResult result;
}
