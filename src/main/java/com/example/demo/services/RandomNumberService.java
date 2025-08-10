package com.example.demo.services;

import com.example.demo.entities.Rules;

import java.util.List;

public interface RandomNumberService {
    List<Integer> generateCode(Rules rules);
}
