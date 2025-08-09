package com.example.demo.repositories;

import com.example.demo.entities.Game;
import org.springframework.stereotype.Repository;

import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentMap;

@Repository
/*
    For this demo I will be storing everything in memory using a concurrent map to save
    each game instance to its corresponding game id.
 */
public class GameRepository {
    private ConcurrentMap<Long, Game> map = new ConcurrentHashMap<>();

    public void save(Game game) {
        map.put(game.getId(), game);
    }


}
