package com.example.demo.services.impl;

import com.example.demo.entities.*;
import com.example.demo.exceptions.GameCompletedException;
import com.example.demo.exceptions.GameNotFoundException;
import com.example.demo.exceptions.InvalidGuessException;
import com.example.demo.mappers.GameMapper;
import com.example.demo.mappers.RulesMapper;
import com.example.demo.models.GameResponseDTO;
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
    private final GameMapper gameMapper;

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
    public GameResponseDTO createGame(RuleDTO rulesDTO) {
        Long id =  currentGameId.getAndIncrement();
        Rules rules = rulesMapper.dtoToEntity(rulesDTO);
        List<Integer> code = randomNumberService.generateCode(rules);
        Game game = new Game(id, rules, code);
        gameRepository.save(game);
        return gameMapper.gameToDTO(game);
    }

    // helper method to check for wins
    // returns true if won, and false otherwise.
    private boolean checkWin(GuessRecord record, Game game) {
        int codeLength = game.getRules().getCodeLength();
        int correctLocation = record.getResult().getCorrectLocations();
        int correctNumbers = record.getResult().getCorrectNumbers();

        return codeLength == correctLocation && codeLength == correctNumbers;
    }

    // helper method to check for losses
    // returns true if lost, false otherwise
    // losses occur when the user submits the final guess attempt and it is incorrect
    private boolean checkLoss(GuessRecord record, Game game) {
        int codeLength = game.getRules().getCodeLength();
        int correctLocation = record.getResult().getCorrectLocations();
        int correctNumbers = record.getResult().getCorrectNumbers();

        // if it wasn't the users last attempts, we know they still have guesses left
        // return false
        if(game.getAttempts() != game.getRules().getMaxAttempts()) {
            return false;
        }

        // if it was their last attempt, return false if either the # of correct locations or # of correct numbers is
        // not equal to the length of the code
        return codeLength != correctLocation || codeLength != correctNumbers;
    }

    // handles validation logic for the guess request. Throws exception if invalid
    // ensures length matches the code length and that the digits are within bounds
    private void validateGuess(GuessRequestDTO dto, Game game) {
        List<Integer> guess = dto.getGuess();
        if(guess == null) {
            throw new InvalidGuessException("Guess cannot be empty!");
        }

        Rules rules = game.getRules();
        int codeLength = rules.getCodeLength();
        int maxDigit = rules.getMaxDigit();

        if(codeLength != guess.size()) {
            throw new InvalidGuessException("Guess length mismatch! Expected: " + game.getRules().getCodeLength() +
                    "- " +
                    "but received: " + guess.size() + " digits!");
        }

        // checks to ensure that no digit is null, greater than the user set Max Digit or less than 0
        for(Integer d : guess) {
            if(d == null) {
                throw new InvalidGuessException("Guess contains a null digit");
            }
            if(d > maxDigit) {
                throw new InvalidGuessException("Digits can not be greater than: " + maxDigit);
            }
            if(d < 0) {
                throw new InvalidGuessException("Digits can not be less than 0");
            }
        }
    }

    @Override
    // method to submit a guess to a specified game instance
    // it returns the modified game instance, or throws an error if the game is completed or not found.
    public GameResponseDTO submitGuess(GuessRequestDTO guessRequestDTO) {
        Game game = findGameById(guessRequestDTO.getGameId());

        // check to ensure game is in progress
        if(game.getStatus() != GameStatus.IN_PROGRESS) {
            // game is completed, throw error
            throw new GameCompletedException("Game is already completed. Start a new game to continue playing!");
        }

        // throws error if guess is invalid, otherwise returns nothing
        validateGuess(guessRequestDTO, game);

        GuessRecord result = score(guessRequestDTO.getGuess(), game);

        // add guess to history and increment attempts
        game.getHistory().add(result);
        game.setAttempts(game.getAttempts()+1);

        // check for win and set game status to WON if true
        if(checkWin(result, game)) {
            game.setStatus(GameStatus.WON);
        } else if(checkLoss(result, game)) {
            // check for loss and set status to LOST if true
            game.setStatus(GameStatus.LOST);
        }


        return gameMapper.gameToDTO(game);
    }

    // helper method to check correct numbers and location of the guess
    // returns a `GuessRecord`
    private GuessRecord score(List<Integer> guess, Game game) {
         int maxDigit = game.getRules().getMaxDigit();

         // arrays to store the frequency of each digit in both the code and the guess
        // this way we can count how many correct digits there are that are not in the correct location
         int[] codeFreq = new int[maxDigit+1];
         int[] guessFreq = new int[maxDigit+1];

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
        for(int i = 0; i <= maxDigit; i++) {
            // for each possible digit from 0 - maxDigit, we add the minimum frequency value to the counter
            // `correctDigitWrongLocation`. This way we don't over or under count the frequencies with duplicates.
            correctDigitWrongLocation += Math.min(codeFreq[i], guessFreq[i]);
        }

        GuessResult result = new GuessResult(correctDigitWrongLocation + correctDigitAndLocation, correctDigitAndLocation);
        return new GuessRecord(guess, result);
    }
}
