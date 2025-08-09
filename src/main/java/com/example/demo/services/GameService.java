package com.example.demo.services;

import com.example.demo.entities.Game;
import com.example.demo.entities.Rules;
import com.example.demo.mappers.RulesMapper;
import com.example.demo.models.RuleDTO;
import com.example.demo.repositories.GameRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.concurrent.atomic.AtomicLong;

@Service
@RequiredArgsConstructor
public class GameService {
    private final RandomNumberService randomNumberService;
    private final RulesMapper rulesMapper;
    private final GameRepository gameRepository;

    // thread safe long value to hold and increment current gameId
    private AtomicLong currentGameId = new AtomicLong(0);


    // creates the game and returns the game unique Id
    // the client will store the id and use it as a token to start/resume their game
    // this method will also store the game instance into the GameRepository, which will be in memory for now.
    public Long createGame(RuleDTO rulesDTO) {
        Long id =  currentGameId.getAndIncrement();
        Rules rules = rulesMapper.dtoToEntity(rulesDTO);
        List<Integer> code = randomNumberService.generateCode(rules);
        Game game = new Game(id, rules, code);
        gameRepository.save(game);
        return id;
    }
}
