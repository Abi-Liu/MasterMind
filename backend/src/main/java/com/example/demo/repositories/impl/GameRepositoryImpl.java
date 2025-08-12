package com.example.demo.repositories.impl;

import com.example.demo.entities.Game;
import com.example.demo.repositories.GameRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentMap;

@Repository
/*
    For this demo I will be storing everything in memory using a concurrent map to save
    each game instance to its corresponding game id.
 */
public class GameRepositoryImpl implements GameRepository {
    private ConcurrentMap<Long, Game> map = new ConcurrentHashMap<>();

    @Override
    public void save(Game game) {
        map.put(game.getId(), game);
    }

    @Override
    public Optional<Game> findById(Long id) {
        return Optional.ofNullable(map.get(id));
    }
}
