package com.example.demo.services.impl;

import com.example.demo.entities.*;
import com.example.demo.exceptions.GameCompletedException;
import com.example.demo.exceptions.GameNotFoundException;
import com.example.demo.exceptions.InvalidGuessException;
import com.example.demo.exceptions.MaxHintsTooLargeException;
import com.example.demo.mappers.GameMapper;
import com.example.demo.mappers.RulesMapper;
import com.example.demo.models.GameResponseDTO;
import com.example.demo.models.GuessRequestDTO;
import com.example.demo.models.RuleDTO;
import com.example.demo.repositories.GameRepository;
import com.example.demo.services.RandomNumberService;
import com.example.demo.utils.GameTestBuilder;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;


public class GameServiceImplTest {
    @Mock
    private RandomNumberService randomNumberService;

    @Mock
    private RulesMapper rulesMapper;

    @Mock
    private GameRepository gameRepository;

    @Mock
    private GameMapper gameMapper;

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
    void testFindGameByIdSuccess() {
        Rules rules = new Rules(4, 7, 10);
        Game game = new Game(1l, rules, List.of(1,1,2,3));

        when(gameRepository.findById(1l)).thenReturn(Optional.of(game));
        Game result = assertDoesNotThrow(() -> gameService.findGameById(1l));

        assertEquals(result.getId(), 1l);
    }


    @Test
    void testFindGameByIdFail() {
        when(gameRepository.findById(1l)).thenReturn(Optional.ofNullable(null));
        assertThrows(GameNotFoundException.class, () -> gameService.findGameById(1l));
    }
    @Test
    void testCreateGame() {
        RuleDTO ruleDTO = new RuleDTO(4, 7, 10);
        Rules rules = new Rules(4, 7, 10);
        List<Integer> code = List.of(1, 1, 2, 5);

        when(rulesMapper.dtoToEntity(ruleDTO)).thenReturn(rules);
        when(randomNumberService.generateCode(rules)).thenReturn(code);

        when(gameMapper.gameToDTO(any(Game.class))).thenAnswer(inv -> {
            Game g = inv.getArgument(0);
            return new GameResponseDTO(g.getId(), ruleDTO, g.getStatus(), g.getAttempts(), g.getHistory());
        });

        GameResponseDTO first = gameService.createGame(ruleDTO);
        GameResponseDTO second = gameService.createGame(ruleDTO);

        assertEquals(1l, first.getId());
        assertEquals(2l, second.getId());

        assertEquals(GameStatus.IN_PROGRESS, first.getStatus());
        assertEquals(0, first.getAttempts());
        assertEquals(ruleDTO, first.getRules());

        verify(gameRepository, times(2)).save(any(Game.class));
        verify(rulesMapper, times(2)).dtoToEntity(ruleDTO);
        verify(randomNumberService, times(2)).generateCode(rules);
        verify(gameMapper, times(2)).gameToDTO(any(Game.class));
    }

    @Test
    void testCreateGameHintsTooLarge() {
        RuleDTO dto = new RuleDTO(4, 9, 10, 5);

        assertThrows(MaxHintsTooLargeException.class, () -> gameService.createGame(dto));
    }

    @Test
    void testCreateGameHintsSameAsCode() {
        RuleDTO dto = new RuleDTO(4, 9, 10, 4);

        assertThrows(MaxHintsTooLargeException.class, () -> gameService.createGame(dto));
    }

    @Test
    void testSubmitGuess() {
        Rules rules = new Rules(4, 7, 10);
        Game game = new Game(1l, rules, List.of(1,1,2,3));
        GuessRequestDTO guess = new GuessRequestDTO(1l, List.of(1,2,3,5));

        when(gameRepository.findById(1l)).thenReturn(Optional.of(game));

        when(gameMapper.gameToDTO(any(Game.class))).thenAnswer(inv -> {
            Game g = inv.getArgument(0);
            return new GameResponseDTO(g.getId(), new RuleDTO(rules.getCodeLength(), rules.getMaxDigit(),
                    rules.getMaxAttempts()), g.getStatus(), g.getAttempts(), g.getHistory());
        });

        // history should be empty
        assertEquals(game.getHistory().size(), 0);

        GameResponseDTO res = gameService.submitGuess(guess);

        assertEquals(res.getHistory().size(), 1);

        GuessRecord record = res.getHistory().get(0);
        assertEquals(record.getResult().getCorrectLocations(), 1);
        assertEquals(record.getResult().getCorrectNumbers(), 3);

        verify(gameRepository).findById(1l);
        verify(gameMapper).gameToDTO(any(Game.class));
    }

    @Test
    void testSubmitGuessAllCorrect() {
        Rules rules = new Rules(4, 7, 10);
        Game game = new Game(1l, rules, List.of(1, 1, 2, 3));
        GuessRequestDTO guess = new GuessRequestDTO(1l, List.of(1, 1, 2, 3));

        when(gameRepository.findById(1l)).thenReturn(Optional.of(game));
        when(gameMapper.gameToDTO(any(Game.class))).thenAnswer(inv -> {
            Game g = inv.getArgument(0);
            return new GameResponseDTO(
                    g.getId(),
                    new RuleDTO(rules.getCodeLength(), rules.getMaxDigit(), rules.getMaxAttempts()),
                    g.getStatus(),
                    g.getAttempts(),
                    g.getHistory()
            );
        });

        int attemptsBefore = game.getAttempts();

        GameResponseDTO res = gameService.submitGuess(guess);

        assertEquals(attemptsBefore + 1, res.getAttempts());
        assertEquals(1, res.getHistory().size());
        assertEquals(GameStatus.WON, res.getStatus());

        GuessRecord record = res.getHistory().get(0);
        assertEquals(4, record.getResult().getCorrectLocations());
        assertEquals(4, record.getResult().getCorrectNumbers());

        verify(gameRepository).findById(1l);
        verify(gameMapper).gameToDTO(any(Game.class));
    }

    @Test
    void testSubmitGuessAllIncorrect() {
        Rules rules = new Rules(4, 7, 10);
        Game game = new Game(1l, rules, List.of(1, 1, 2, 3));
        GuessRequestDTO guess = new GuessRequestDTO(1l, List.of(5, 5, 4, 7));

        when(gameRepository.findById(1l)).thenReturn(Optional.of(game));
        when(gameMapper.gameToDTO(any(Game.class))).thenAnswer(inv -> {
            Game g = inv.getArgument(0);
            return new GameResponseDTO(
                    g.getId(),
                    new RuleDTO(rules.getCodeLength(), rules.getMaxDigit(), rules.getMaxAttempts()),
                    g.getStatus(),
                    g.getAttempts(),
                    g.getHistory()
            );
        });

        int attemptsBefore = game.getAttempts();

        GameResponseDTO res = gameService.submitGuess(guess);

        assertEquals(attemptsBefore + 1, res.getAttempts());
        assertEquals(1, res.getHistory().size());
        assertEquals(GameStatus.IN_PROGRESS, res.getStatus());

        GuessRecord record = res.getHistory().get(0);
        assertEquals(0, record.getResult().getCorrectLocations());
        assertEquals(0, record.getResult().getCorrectNumbers());

        verify(gameRepository).findById(1l);
        verify(gameMapper).gameToDTO(any(Game.class));
    }

    @Test
    void testSubmitGuessAllDuplicates() {
        Rules rules = new Rules(4, 7, 10);
        Game game = new Game(1l, rules, List.of(7, 7, 7, 7));
        GuessRequestDTO guess = new GuessRequestDTO(1l, List.of(1, 5, 4, 7));

        when(gameRepository.findById(1l)).thenReturn(Optional.of(game));
        when(gameMapper.gameToDTO(any(Game.class))).thenAnswer(inv -> {
            Game g = inv.getArgument(0);
            return new GameResponseDTO(
                    g.getId(),
                    new RuleDTO(rules.getCodeLength(), rules.getMaxDigit(), rules.getMaxAttempts()),
                    g.getStatus(),
                    g.getAttempts(),
                    g.getHistory()
            );
        });

        GameResponseDTO res = gameService.submitGuess(guess);

        assertEquals(1, res.getHistory().size());
        assertEquals(GameStatus.IN_PROGRESS, res.getStatus());

        GuessRecord record = res.getHistory().get(0);
        assertEquals(1, record.getResult().getCorrectLocations());
        assertEquals(1, record.getResult().getCorrectNumbers());

        verify(gameRepository).findById(1l);
        verify(gameMapper).gameToDTO(any(Game.class));
    }

    @Test
    void testSubmitGuessOnWonGame() {
        Rules rules = new Rules(4, 7, 10);
        Game game = new Game(1l, rules, List.of(1, 1, 2, 3));
        game.setStatus(GameStatus.WON);

        when(gameRepository.findById(1l)).thenReturn(Optional.of(game));

        GuessRequestDTO guess = new GuessRequestDTO(1l, List.of(1, 2, 3, 5));
        assertThrows(GameCompletedException.class, () -> gameService.submitGuess(guess));

        verify(gameRepository).findById(1l);
        verifyNoInteractions(gameMapper);
    }

    @Test
    void testSubmitGuessOnLostGame() {
        Rules rules = new Rules(4, 7, 10);
        Game game = new Game(1l, rules, List.of(1, 1, 2, 3));
        game.setStatus(GameStatus.LOST);

        when(gameRepository.findById(1l)).thenReturn(Optional.of(game));

        GuessRequestDTO guess = new GuessRequestDTO(1l, List.of(1, 2, 3, 5));
        assertThrows(GameCompletedException.class, () -> gameService.submitGuess(guess));

        verify(gameRepository).findById(1l);
        verifyNoInteractions(gameMapper);
    }

    @Test
    void testSubmitGuessUserWin() {
        Rules rules = new Rules(4, 7, 10);
        Game game = new Game(1l, rules, List.of(1, 1, 2, 3));

        when(gameRepository.findById(1l)).thenReturn(Optional.of(game));
        when(gameMapper.gameToDTO(any(Game.class))).thenAnswer(inv -> {
            Game g = inv.getArgument(0);
            return new GameResponseDTO(
                    g.getId(),
                    new RuleDTO(rules.getCodeLength(), rules.getMaxDigit(), rules.getMaxAttempts()),
                    g.getStatus(),
                    g.getAttempts(),
                    g.getHistory()
            );
        });

        GameResponseDTO res = gameService.submitGuess(new GuessRequestDTO(1l, List.of(1, 1, 2, 3)));

        assertEquals(GameStatus.WON, res.getStatus());
        assertEquals(1, res.getHistory().size());

        verify(gameRepository).findById(1l);
        verify(gameMapper).gameToDTO(any(Game.class));
    }

    @Test
    void testSubmitGuessUserLoses() {
        Rules rules = new Rules(4, 7, 10);
        Game game = new Game(1l, rules, List.of(1, 1, 2, 3));
        game.setAttempts(9); // last attempt

        when(gameRepository.findById(1l)).thenReturn(Optional.of(game));
        when(gameMapper.gameToDTO(any(Game.class))).thenAnswer(inv -> {
            Game g = inv.getArgument(0);
            return new GameResponseDTO(
                    g.getId(),
                    new RuleDTO(rules.getCodeLength(), rules.getMaxDigit(), rules.getMaxAttempts()),
                    g.getStatus(),
                    g.getAttempts(),
                    g.getHistory()
            );
        });

        GameResponseDTO res = gameService.submitGuess(new GuessRequestDTO(1l, List.of(1, 1, 3, 3)));

        assertEquals(GameStatus.LOST, res.getStatus());
        assertEquals(10, res.getAttempts());
        assertEquals(1, res.getHistory().size());

        verify(gameRepository).findById(1l);
        verify(gameMapper).gameToDTO(any(Game.class));
    }

    @Test
    void testSubmitGuessNullList() {
        Rules rules = new Rules(4, 7, 10);
        Game game = new Game(1l, rules, List.of(1, 1, 2, 3));
        when(gameRepository.findById(1l)).thenReturn(Optional.of(game));

        GuessRequestDTO guess = new GuessRequestDTO(1l, null);
        assertThrows(InvalidGuessException.class, () -> gameService.submitGuess(guess));

        verify(gameRepository).findById(1l);
        verifyNoInteractions(gameMapper);
    }

    @Test
    void testSubmitGuessEmptyList() {
        Rules rules = new Rules(4, 7, 10);
        Game game = new Game(1l, rules, List.of(1, 1, 2, 3));
        when(gameRepository.findById(1l)).thenReturn(Optional.of(game));

        GuessRequestDTO guess = new GuessRequestDTO(1l, List.of());
        assertThrows(InvalidGuessException.class, () -> gameService.submitGuess(guess));

        verify(gameRepository).findById(1l);
        verifyNoInteractions(gameMapper);
    }

    @Test
    void testSubmitGuessNullDigit() {
        Rules rules = new Rules(4, 7, 10);
        Game game = new Game(1l, rules, List.of(1, 1, 2, 3));
        when(gameRepository.findById(1l)).thenReturn(Optional.of(game));

        List<Integer> list = new ArrayList<>();
        list.add(null);
        list.add(1);
        list.add(1);
        list.add(4);

        GuessRequestDTO guess = new GuessRequestDTO(1l, list);
        assertThrows(InvalidGuessException.class, () -> gameService.submitGuess(guess));

        verify(gameRepository).findById(1l);
        verifyNoInteractions(gameMapper);
    }

    @Test
    void testSubmitGuessDigitTooLarge() {
        Rules rules = new Rules(4, 7, 10);
        Game game = new Game(1l, rules, List.of(1, 1, 2, 3));
        when(gameRepository.findById(1l)).thenReturn(Optional.of(game));

        GuessRequestDTO guess = new GuessRequestDTO(1l, List.of(1, 1, 8, 3));
        assertThrows(InvalidGuessException.class, () -> gameService.submitGuess(guess));

        verify(gameRepository).findById(1l);
        verifyNoInteractions(gameMapper);
    }

    @Test
    void testSubmitGuessNegativeDigit() {
        Rules rules = new Rules(4, 7, 10);
        Game game = new Game(1l, rules, List.of(1, 1, 2, 3));
        when(gameRepository.findById(1l)).thenReturn(Optional.of(game));

        GuessRequestDTO guess = new GuessRequestDTO(1l, List.of(1, -1, 3, 3));
        assertThrows(InvalidGuessException.class, () -> gameService.submitGuess(guess));

        verify(gameRepository).findById(1l);
        verifyNoInteractions(gameMapper);
    }

}
