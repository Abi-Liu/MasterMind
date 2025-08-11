package com.example.demo.mappers;

import com.example.demo.entities.Game;
import com.example.demo.mappers.helpers.HintsHelper;
import com.example.demo.models.GameResponseDTO;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

@Mapper(componentModel = "spring", uses = {RulesMapper.class, HintsHelper.class})
public interface GameMapper {
    @Mapping(target = "hints", source = "hints", qualifiedByName = "hintsToList")
    GameResponseDTO gameToDTO(Game game);
}
