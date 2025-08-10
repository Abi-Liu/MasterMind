package com.example.demo.services.impl;

import com.example.demo.entities.Game;
import com.example.demo.entities.GuessRecord;
import com.example.demo.entities.GuessResult;
import com.example.demo.entities.Rules;
import com.example.demo.mappers.RulesMapper;
import com.example.demo.models.GuessRequestDTO;
import com.example.demo.models.RuleDTO;
import com.example.demo.repositories.GameRepository;
import com.example.demo.services.RandomNumberService;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;

import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.Mockito.only;
import static org.mockito.Mockito.when;


public class GameServiceImplTest {
    @Mock
    private RandomNumberService randomNumberService;

    @Mock
    private RulesMapper rulesMapper;

    @Mock
    private GameRepository gameRepository;

    @InjectMocks
    private GameServiceImpl gameService;

    private AutoCloseable closeable;

    @BeforeEach
    void setup() {
        closeable = MockitoAnnotations.openMocks(this);
    }

    @AfterEach
    void tearDown() throws Exception {
        closeable.close();
    }

    @Test
    void testCreateGame() {
        RuleDTO ruleDTO = new RuleDTO(4, 7, 10);
        Rules rules = new Rules(4, 7, 10);
        List<Integer> code = List.of(1,1,2,5);
        when(rulesMapper.dtoToEntity(ruleDTO)).thenReturn(rules);
        when(randomNumberService.generateCode(rules)).thenReturn(code);

        Long gameId = gameService.createGame(ruleDTO);

        Long nextGameId = gameService.createGame(ruleDTO);

        assertEquals(gameId, 0l);

        assertEquals(nextGameId, 1l);
    }

    @Test
    void testSubmitGuess() {
        Rules rules = new Rules(4, 7, 10);
        Game game = new Game(1l, rules, List.of(1,1,2,3));
        GuessRequestDTO guess = new GuessRequestDTO(1l, List.of(1,2,3,5));

        when(gameRepository.findById(1l)).thenReturn(Optional.of(game));

        // history should be empty
        assertEquals(game.getHistory().size(), 0);

        game = gameService.submitGuess(guess);

        assertEquals(game.getHistory().size(), 1);

        GuessRecord record = game.getHistory().get(0);
        assertEquals(record.getResult().getCorrectLocations(), 1);
        assertEquals(record.getResult().getCorrectNumbers(), 3);
    }
}
