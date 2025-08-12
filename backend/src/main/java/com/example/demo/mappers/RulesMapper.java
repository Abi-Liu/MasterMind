package com.example.demo.mappers;

import com.example.demo.entities.Rules;
import com.example.demo.models.RuleDTO;
import org.mapstruct.Mapper;

@Mapper(componentModel = "spring")
public interface RulesMapper {
    RuleDTO entityToDTO(Rules rules);

    Rules dtoToEntity(RuleDTO DTO);
}
