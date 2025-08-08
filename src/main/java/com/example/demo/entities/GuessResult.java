package com.example.demo.entities;

import lombok.Data;

@Data
/*
    Stores the result of each guess
    How many numbers are correct and how many correct numbers are in the correct locations
 */
public class GuessResult {
    private int correctNumbers;
    private int correctLocations;
}
