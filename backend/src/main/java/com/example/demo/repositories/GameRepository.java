package com.example.demo.repositories;

import com.example.demo.entities.Game;

import java.util.Optional;

public interface GameRepository {
    void save(Game game);

    Optional<Game> findById(Long id);
}
