package com.example.demo.services.impl;

import com.example.demo.entities.*;
import com.example.demo.exceptions.GameCompletedException;
import com.example.demo.exceptions.GameNotFoundException;
import com.example.demo.mappers.RulesMapper;
import com.example.demo.models.GuessRequestDTO;
import com.example.demo.models.RuleDTO;
import com.example.demo.repositories.GameRepository;
import com.example.demo.services.GameService;
import com.example.demo.services.RandomNumberService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;
import java.util.concurrent.atomic.AtomicLong;

@Service
@RequiredArgsConstructor
public class GameServiceImpl implements GameService {
    private final RandomNumberService randomNumberService;
    private final RulesMapper rulesMapper;
    private final GameRepository gameRepository;

    // thread safe long value to hold and increment current gameId
    private AtomicLong currentGameId = new AtomicLong(0);

    // method to retrieve games by id. Throws an exception if the game is not found
    // otherwise returns the game instance.
    public Game findGameById(Long id) {
        Optional<Game> gameOptional = gameRepository.findById(id);

        if(gameOptional.isEmpty()) {
            // throw new exception, game not found
            throw new GameNotFoundException("Could not find game with ID: " + id);
        }

        return gameOptional.get();
    }

    // creates the game and returns the game unique Id
    // the client will store the id and use it as a token to start/resume their game
    // this method will also store the game instance into the GameRepository, which will be in memory for now.
    @Override
    public Long createGame(RuleDTO rulesDTO) {
        Long id =  currentGameId.getAndIncrement();
        Rules rules = rulesMapper.dtoToEntity(rulesDTO);
        List<Integer> code = randomNumberService.generateCode(rules);
        Game game = new Game(id, rules, code);
        gameRepository.save(game);
        return id;
    }

    @Override
    // method to submit a guess to a specified game instance
    // it returns the modified game instance, or throws an error if the game is completed or not found.
    public Game submitGuess(GuessRequestDTO guessRequestDTO) {
        Game game = findGameById(guessRequestDTO.getGameId());

        // check to ensure game is in progress
        if(game.getStatus() != GameStatus.IN_PROGRESS) {
            // game is completed, throw error
            throw new GameCompletedException("Game is already completed. Start a new game to continue playing!");
        }

        GuessRecord result = score(guessRequestDTO.getGuess(), game);

        game.getHistory().add(result);
        game.setAttempts(game.getAttempts()+1);
        return game;
    }

    // helper method to check correct numbers and location of the guess
    // returns a `GuessRecord`
    private GuessRecord score(List<Integer> guess, Game game) {
         int maxDigit = game.getRules().getMaxDigit();

         // arrays to store the frequency of each digit in both the code and the guess
        // this way we can count how many correct digits there are that are not in the correct location
         int[] codeFreq = new int[maxDigit];
         int[] guessFreq = new int[maxDigit];

         // counters to store how many correct digits and correct locations
         int correctDigitAndLocation = 0;
         int correctDigitWrongLocation = 0;

         // first we need to check how many correct digit and correct locations the user has guessed
        for(int i = 0; i < guess.size(); i++) {
             int codeDigit = game.getCode().get(i);
             int guessDigit = guess.get(i);

             // case where the digit is correct and in the same location
             if(codeDigit == guessDigit) {
                correctDigitAndLocation++;
             } else {
                 // increment the guessFreq array at index `guessDigit`
                 guessFreq[guessDigit]++;
                 //increment the codeFreq array int index `codeDigit`
                 codeFreq[codeDigit]++;
             }
        }

        // now we check for correct digit but wrong location
        // here we can utilize the frequency arrays we created in the previous step
        for(int i = 0; i < maxDigit; i++) {
            // for each possible digit from 0 - maxDigit, we add the minimum frequency value to the counter
            // `correctDigitWrongLocation`. This way we don't over or under count the frequencies with duplicates.
            correctDigitWrongLocation += Math.min(codeFreq[i], guessFreq[i]);
        }

        GuessResult result = new GuessResult(correctDigitWrongLocation + correctDigitAndLocation, correctDigitAndLocation);
        return new GuessRecord(guess, result);
    }
}
