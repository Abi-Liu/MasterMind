package com.example.demo.services.impl;

import com.example.demo.entities.Game;
import com.example.demo.exceptions.GameCompletedException;
import com.example.demo.exceptions.OutOfHintsException;
import com.example.demo.models.HintResponseDTO;
import com.example.demo.repositories.GameRepository;
import com.example.demo.services.GameService;
import com.example.demo.services.HintService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.Map;

@Service
@RequiredArgsConstructor
public class HintServiceImpl implements HintService {
    private final GameService gameService;
    private final GameRepository gameRepository;

    @Override
    public HintResponseDTO useHint(Long id) {
        Game game = gameService.findGameById(id);

        if(!gameService.isGameInProgress(game)) {
            throw new GameCompletedException("Game is already over.");
        }

        if(!game.canUseHint()) {
            // user is out of hints. throw OutOfHintsException
            throw new OutOfHintsException("You are out of hints! Good luck!");
        }

        // create logic to  find an index and give its digit
        // for this we can just start from the first position of the code and check to see if it's position has
        // already been given by a hint, if so move onto the next index
        int codeLength = game.getRules().getCodeLength();
        Map<Integer, Integer> map = game.getHints();
        HintResponseDTO hint = new HintResponseDTO();
        for(int i = 0; i < codeLength; i++) {
            // if a hint has not revealed the digit at this location, reveal it
            if(map.get(i) == null) {
                int digit = game.getCode().get(i);
                hint.setLocation(i);
                hint.setDigit(digit);

                // insert the revealed digit and location into the hints map increment hintsUsed and update the game
                map.put(i, digit);
                game.setHintsUsed(game.getHintsUsed() + 1);
                gameRepository.save(game);

                break;
            }
        }

        return hint;
    }
}
