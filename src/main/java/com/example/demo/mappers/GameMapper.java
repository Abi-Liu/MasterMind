package com.example.demo.mappers;

import com.example.demo.entities.Game;
import com.example.demo.models.GameResponseDTO;
import org.mapstruct.Mapper;

@Mapper(componentModel = "spring")
public interface GameMapper {
    GameResponseDTO gameToDTO(Game game);

    Game DtoToGame(GameResponseDTO game);
}
