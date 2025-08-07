package com.example.demo.services;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.concurrent.atomic.AtomicLong;

@Service
@RequiredArgsConstructor
public class GameService {
    // thread safe long value to hold and increment current gameId
    private AtomicLong currentGameId = new AtomicLong(0);

    public Long createGame() {
        return currentGameId.getAndIncrement();
    }
}
