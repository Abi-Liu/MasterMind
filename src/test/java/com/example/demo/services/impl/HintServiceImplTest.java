package com.example.demo.services.impl;

import com.example.demo.entities.Game;
import com.example.demo.exceptions.OutOfHintsException;
import com.example.demo.models.HintResponseDTO;
import com.example.demo.repositories.GameRepository;
import com.example.demo.services.GameService;
import com.example.demo.utils.GameTestBuilder;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;

import java.util.List;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;


public class HintServiceImplTest {
    @Mock
    private GameService gameService;

    @Mock
    private GameRepository gameRepository;

    @InjectMocks
    private HintServiceImpl hintService;

    private AutoCloseable closeable;

    @BeforeEach
    void setUp() {
        closeable = MockitoAnnotations.openMocks(this);
    }

    @AfterEach
    void tearDown() throws Exception {
        closeable.close();
    }

    @Test
    void testUseHintSuccess() {
        Game game = GameTestBuilder.createGame(0l, List.of(1,2,3,4));
        when(gameService.findGameById(0l)).thenReturn(game);
        when(gameService.isGameInProgress(game)).thenReturn(true);

        HintResponseDTO res = assertDoesNotThrow(() -> hintService.useHint(game.getId()));

        assertEquals(0, res.getLocation());
        assertEquals(1, res.getDigit());

        int codeLength = game.getRules().getCodeLength();
        int maxDigit = game.getRules().getMaxDigit();
        assertTrue(res.getDigit() <= maxDigit);
        assertTrue(res.getLocation() < codeLength);
    }

    @Test
    void testUseHintsOutOfHints() {
        Game game = GameTestBuilder.createGame(0l, List.of(1,2,3,4));
        game.setHintsUsed(game.getMaxHints());

        when(gameService.findGameById(0l)).thenReturn(game);
        when(gameService.isGameInProgress(game)).thenReturn(true);

        assertThrows(OutOfHintsException.class, () -> hintService.useHint(game.getId()));
    }

    @Test
    void testUseHintsMultiple(){
        Game game = GameTestBuilder.createGame(0l, List.of(3,2,1,5));
        game.setMaxHints(3);

        when(gameService.findGameById(0l)).thenReturn(game);
        when(gameService.isGameInProgress(game)).thenReturn(true);

        HintResponseDTO res = assertDoesNotThrow(() -> hintService.useHint(game.getId()));
        assertEquals(0, res.getLocation());
        assertEquals(3, res.getDigit());

        res = assertDoesNotThrow(() -> hintService.useHint(game.getId()));
        assertEquals(1, res.getLocation());
        assertEquals(2, res.getDigit());

        res = assertDoesNotThrow(() -> hintService.useHint(game.getId()));
        assertEquals(2, res.getLocation());
        assertEquals(1, res.getDigit());

        assertThrows(OutOfHintsException.class, () -> hintService.useHint(game.getId()));
    }
}
