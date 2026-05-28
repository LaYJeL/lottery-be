package com.game.lottery.service;

import com.game.lottery.dto.CompetitionDto;
import com.game.lottery.dto.CompetitionEntryDto;
import com.game.lottery.exception.CompetitionNotFoundException;
import com.game.lottery.mapper.CompetitionEntryMapper;
import com.game.lottery.mapper.CompetitionMapper;
import com.game.lottery.model.Competition;
import com.game.lottery.model.CompetitionEntry;
import com.game.lottery.model.UserProfile;
import com.game.lottery.repository.CompetitionEntryRepository;
import com.game.lottery.repository.CompetitionRepository;
import com.game.lottery.repository.UserRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.Pageable;

import java.util.Collections;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class CompetitionServiceTest {

    @Mock
    private CompetitionRepository competitionRepository;

    @Mock
    private CompetitionEntryRepository competitionEntryRepository;

    @Mock
    private WalletService walletService;

    @Mock
    private UserRepository userRepository;

    @Mock
    private CompetitionMapper competitionMapper;

    @Mock
    private CompetitionEntryMapper competitionEntryMapper;

    @InjectMocks
    private CompetitionService competitionService;

    private UUID userId;
    private UUID competitionId;
    private Competition competition;
    private CompetitionEntry competitionEntry;

    @BeforeEach
    void setUp() {
        userId = UUID.randomUUID();
        competitionId = UUID.randomUUID();
        competition = Competition.builder()
                .id(competitionId)
                .title("Test Competition")
                .status(com.game.lottery.enums.CompetitionStatus.ACTIVE)
                .entryFee(java.math.BigDecimal.ZERO)
                .participantsCount(0)
                .participantsCount(0)
                .build();
        competitionEntry = new CompetitionEntry();
        competitionEntry.setId(UUID.randomUUID());
        competitionEntry.setCompetition(competition);
    }

    @Test
    void getCompetitionById_shouldReturnCompetition_whenExists() {
        when(competitionRepository.findById(competitionId)).thenReturn(Optional.of(competition));
        when(competitionEntryRepository.findByCompetition_IdAndUser_UserId(competitionId, userId))
                .thenReturn(Optional.empty());
        when(competitionMapper.toDto(eq(competition), eq(false))).thenReturn(CompetitionDto.builder()
                .id(competitionId)
                .title("Test Competition")
                .isEntered(false)
                .participantsCount(0)
                .build());

        CompetitionDto result = competitionService.getCompetitionById(userId, competitionId);

        assertThat(result).isNotNull();
        assertThat(result.getId()).isEqualTo(competitionId);
        assertThat(result.isEntered()).isFalse();
    }

    @Test
    void getCompetitionById_shouldReturnCompetitionWithEnteredTrue_whenUserHasEntered() {
        when(competitionRepository.findById(competitionId)).thenReturn(Optional.of(competition));
        when(competitionEntryRepository.findByCompetition_IdAndUser_UserId(competitionId, userId))
                .thenReturn(Optional.of(competitionEntry));
        when(competitionMapper.toDto(eq(competition), eq(true))).thenReturn(CompetitionDto.builder()
                .id(competitionId)
                .title("Test Competition")
                .isEntered(true)
                .participantsCount(0)
                .build());

        CompetitionDto result = competitionService.getCompetitionById(userId, competitionId);

        assertThat(result).isNotNull();
        assertThat(result.getId()).isEqualTo(competitionId);
        assertThat(result.isEntered()).isTrue();
    }

    @Test
    void getCompetitionById_shouldThrowException_whenCompetitionNotFound() {
        when(competitionRepository.findById(competitionId)).thenReturn(Optional.empty());

        assertThatThrownBy(() -> competitionService.getCompetitionById(userId, competitionId))
                .isInstanceOf(CompetitionNotFoundException.class)
                .hasMessageContaining("Competition not found");
    }

    @Test
    void joinCompetition_shouldIncrementParticipantsCount_whenUserJoins() {
        UUID userId = UUID.randomUUID();
        UUID competitionId = UUID.randomUUID();
        Competition competition = Competition.builder()
                .id(competitionId)
                .title("Test Competition")
                .status(com.game.lottery.enums.CompetitionStatus.ACTIVE)
                .entryFee(java.math.BigDecimal.ZERO)
                .participantsCount(0)
                .build();

        UserProfile profile = new UserProfile();
        profile.setUserId(userId);
        com.game.lottery.model.User user = new com.game.lottery.model.User();
        user.setProfile(profile);

        when(competitionRepository.findById(competitionId)).thenReturn(Optional.of(competition));
        when(competitionEntryRepository.findByCompetition_IdAndUser_UserId(competitionId, userId))
                .thenReturn(Optional.empty());
        when(userRepository.findById(userId)).thenReturn(Optional.of(user));

        competitionService.joinCompetition(userId, competitionId);

        assertThat(competition.getParticipantsCount()).isEqualTo(1);
        org.mockito.Mockito.verify(competitionRepository).save(competition);
    }

    @Test
    void getActiveCompetitions_shouldReturnPagedResults() {
        Pageable pageable = Pageable.ofSize(10);
        Page<Competition> page = new PageImpl<>(List.of(competition));

        when(competitionRepository.findByStatus(eq(com.game.lottery.enums.CompetitionStatus.ACTIVE),
                any(Pageable.class)))
                .thenReturn(page);
        when(competitionEntryRepository.findByUser_UserId(userId)).thenReturn(Collections.emptyList());
        when(competitionMapper.toDto(any(Competition.class), eq(false))).thenAnswer(i -> {
            Competition c = i.getArgument(0);
            return CompetitionDto.builder()
                    .id(c.getId())
                    .title(c.getTitle())
                    .participantsCount(c.getParticipantsCount())
                    .isEntered(false)
                    .build();
        });

        Page<CompetitionDto> result = competitionService.getActiveCompetitions(userId, pageable);

        assertThat(result).isNotNull();
        assertThat(result.getTotalElements()).isEqualTo(1);
        assertThat(result.getContent().get(0).getId()).isEqualTo(competitionId);
    }

    @Test
    void getMyEntries_Success() {
        when(competitionEntryRepository.findByUser_UserId(userId)).thenReturn(List.of(competitionEntry));
        when(competitionEntryMapper.toDto(competitionEntry)).thenReturn(CompetitionEntryDto.builder().build());

        List<CompetitionEntryDto> result = competitionService.getMyEntries(userId);

        assertThat(result).isNotNull();
        assertThat(result).hasSize(1);
    }
}
