package com.game.lottery.mapper;

import com.game.lottery.dto.CompetitionDto;
import com.game.lottery.model.Competition;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

@Mapper(componentModel = "spring")
public interface CompetitionMapper {

    @Mapping(target = "isEntered", ignore = true)
    CompetitionDto toDto(Competition competition);

    default CompetitionDto toDto(Competition competition, boolean isEntered) {
        CompetitionDto dto = toDto(competition);
        dto.setEntered(isEntered);
        return dto;
    }
}
