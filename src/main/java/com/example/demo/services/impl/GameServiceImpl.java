package com.example.demo.services.impl;

import com.example.demo.entities.Game;
import com.example.demo.entities.GuessResult;
import com.example.demo.entities.Rules;
import com.example.demo.mappers.RulesMapper;
import com.example.demo.models.GuessRequestDTO;
import com.example.demo.models.RuleDTO;
import com.example.demo.repositories.impl.GameRepositoryImpl;
import com.example.demo.services.GameService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;
import java.util.concurrent.atomic.AtomicLong;

@Service
@RequiredArgsConstructor
public class GameServiceImpl implements GameService {
    private final RandomNumberServiceImpl randomNumberService;
    private final RulesMapper rulesMapper;
    private final GameRepositoryImpl gameRepository;

    // thread safe long value to hold and increment current gameId
    private AtomicLong currentGameId = new AtomicLong(0);


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
    public Game submitGuess(GuessRequestDTO guessRequestDTO) {
        Optional<Game> gameOptional = gameRepository.findById(guessRequestDTO.getGameId());

        if(gameOptional.isEmpty()) {
            // game does not exist, throw error
            // throw new error and send to client
        }


    }

    // helper method to check correct numbers and location of the guess
    private GuessResult score(List<Integer> guess, Game game) {
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
            // `correctDigitWrongLocation`. This way we don't over or under count the frequencies.
            correctDigitWrongLocation += Math.min(codeFreq[i], guessFreq[i]);
        }

        GuessResult result = new GuessResult(correctDigitWrongLocation + correctDigitAndLocation, correctDigitAndLocation);
        return result;
    }
}
