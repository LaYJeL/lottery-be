package com.game.lottery.mapper;

import com.game.lottery.dto.CompetitionEntryDto;
import com.game.lottery.model.CompetitionEntry;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

@Mapper(componentModel = "spring")
public interface CompetitionEntryMapper {

    @Mapping(target = "competitionId", source = "competition.id")
    @Mapping(target = "competitionTitle", source = "competition.title")
    @Mapping(target = "competitionType", source = "competition.type")
    CompetitionEntryDto toDto(CompetitionEntry entry);
}
