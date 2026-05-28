package com.game.lottery.service;

import com.game.lottery.dto.CompetitionDto;
import com.game.lottery.dto.CompetitionEntryDto;
import com.game.lottery.enums.CompetitionStatus;
import com.game.lottery.enums.EntryStatus;
import com.game.lottery.exception.AlreadyJoinedException;
import com.game.lottery.exception.CompetitionNotFoundException;
import com.game.lottery.exception.CompetitionNotActiveException;
import com.game.lottery.exception.EntryAlreadySubmittedException;
import com.game.lottery.exception.EntryNotFoundException;
import com.game.lottery.exception.InvalidEntryStateException;
import com.game.lottery.exception.UserNotFoundException;
import com.game.lottery.mapper.CompetitionEntryMapper;
import com.game.lottery.mapper.CompetitionMapper;
import com.game.lottery.model.Competition;
import com.game.lottery.model.CompetitionEntry;
import com.game.lottery.model.UserProfile;
import com.game.lottery.repository.CompetitionEntryRepository;
import com.game.lottery.repository.CompetitionRepository;
import com.game.lottery.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.List;
import java.util.UUID;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class CompetitionService {

    private final CompetitionRepository competitionRepository;
    private final CompetitionEntryRepository competitionEntryRepository;
    private final WalletService walletService;
    private final UserRepository userRepository;
    private final CompetitionMapper competitionMapper;
    private final CompetitionEntryMapper competitionEntryMapper;

    public org.springframework.data.domain.Page<CompetitionDto> getActiveCompetitions(UUID userId,
            org.springframework.data.domain.Pageable pageable) {
        org.springframework.data.domain.Page<Competition> competitions = competitionRepository
                .findByStatus(CompetitionStatus.ACTIVE, pageable);

        // Extract competition IDs from the current page
        List<UUID> competitionIds = competitions.getContent().stream()
                .map(Competition::getId)
                .collect(Collectors.toList());

        // Single optimized query to find which competitions the user has entered
        List<UUID> enteredCompetitionIds = competitionIds.isEmpty()
                ? List.of()
                : competitionEntryRepository.findEnteredCompetitionIds(userId, competitionIds);

        return competitions.map(comp -> competitionMapper.toDto(comp, enteredCompetitionIds.contains(comp.getId())));
    }

    public List<CompetitionDto> getAllCompetitions() {
        return competitionRepository.findAll().stream()
                .map(comp -> competitionMapper.toDto(comp, false)) // Admin view doesn't need isEntered logic usually
                .collect(Collectors.toList());
    }

    public CompetitionDto getCompetitionById(UUID userId, UUID competitionId) {
        Competition competition = competitionRepository.findById(competitionId)
                .orElseThrow(() -> new CompetitionNotFoundException("Competition not found with id: " + competitionId));

        boolean isEntered = competitionEntryRepository.findByCompetition_IdAndUser_UserId(competitionId, userId)
                .isPresent();

        return competitionMapper.toDto(competition, isEntered);
    }

    public List<CompetitionEntryDto> getMyEntries(UUID userId) {
        return competitionEntryRepository.findByUser_UserId(userId).stream()
                .map(competitionEntryMapper::toDto)
                .collect(Collectors.toList());
    }

    @Transactional
    public void joinCompetition(UUID userId, UUID competitionId) {
        Competition competition = competitionRepository.findById(competitionId)
                .orElseThrow(() -> new CompetitionNotFoundException("Competition not found with id: " + competitionId));

        if (competition.getStatus() != CompetitionStatus.ACTIVE) {
            throw new CompetitionNotActiveException("Competition is not active");
        }

        if (competitionEntryRepository.findByCompetition_IdAndUser_UserId(competitionId, userId).isPresent()) {
            throw new AlreadyJoinedException("Already joined this competition");
        }

        // Deduct Fee
        walletService.spendForCompetition(userId, competition.getEntryFee(), "Entry fee for " + competition.getTitle());

        // Create Entry
        UserProfile userProfile = userRepository.findById(userId)
                .orElseThrow(() -> new UserNotFoundException("User not found")).getProfile();

        // Update user stats
        userProfile.setCompetitionEntries(userProfile.getCompetitionEntries() + 1);

        CompetitionEntry entry = CompetitionEntry.builder()
                .competition(competition)
                .user(userProfile)
                .status(EntryStatus.PENDING)
                .build();

        competitionEntryRepository.save(entry);

        // Update participant count
        competition.setParticipantsCount(competition.getParticipantsCount() + 1);
        competitionRepository.save(competition);
    }

    @Transactional
    public void submitEntry(UUID userId, UUID competitionId, String content) {
        CompetitionEntry entry = competitionEntryRepository.findByCompetition_IdAndUser_UserId(competitionId, userId)
                .orElseThrow(() -> new EntryNotFoundException("Entry not found (Did you join?)"));

        if (entry.getStatus() != EntryStatus.PENDING && entry.getStatus() != EntryStatus.REJECTED
                && entry.getStatus() != EntryStatus.UNDER_REVIEW) {
            // Allow resubmission if pending or rejected? Or maybe just pending?
            // Simplification: Allow update if not PUBLISHED/WINNER?
            // Let's say only if PENDING for now.
            if (entry.getStatus() != EntryStatus.PENDING) {
                throw new EntryAlreadySubmittedException("Entry already submitted or processed");
            }
        }

        entry.setContent(content);

        Competition competition = entry.getCompetition();
        if (competition.isRequiresApproval()) {
            entry.setStatus(EntryStatus.UNDER_REVIEW);
        } else {
            entry.setStatus(EntryStatus.PUBLISHED);
        }

        entry.setSubmittedAt(LocalDateTime.now());
        competitionEntryRepository.save(entry);
    }

    // Admin Methods
    @Transactional
    public CompetitionDto createCompetition(CompetitionDto dto) {
        Competition competition = Competition.builder()
                .title(dto.getTitle())
                .type(dto.getType())
                .description(dto.getDescription())
                .prize(dto.getPrize())
                .entryFee(dto.getEntryFee())
                .startTime(dto.getStartTime())
                .endTime(dto.getEndTime())
                .status(CompetitionStatus.DRAFT) // Default to DRAFT
                .imageUrl(dto.getImageUrl())
                .requiresApproval(dto.isRequiresApproval())
                .participantsCount(0)
                .build();

        competition = competitionRepository.save(competition);
        return competitionMapper.toDto(competition, false);
    }

    @Transactional
    public CompetitionDto updateCompetitionStatus(UUID id, CompetitionStatus status) {
        Competition competition = competitionRepository.findById(id)
                .orElseThrow(() -> new CompetitionNotFoundException("Competition not found"));
        competition.setStatus(status);
        competitionRepository.save(competition);
        return competitionMapper.toDto(competition, false);
    }

    @Transactional
    public void approveEntry(UUID competitionId, UUID entryId) {
        CompetitionEntry entry = competitionEntryRepository.findById(entryId)
                .orElseThrow(() -> new EntryNotFoundException("Entry not found"));

        if (!entry.getCompetition().getId().equals(competitionId)) {
            throw new InvalidEntryStateException("Entry does not belong to this competition");
        }

        if (entry.getStatus() != EntryStatus.UNDER_REVIEW) {
            throw new InvalidEntryStateException("Entry is not under review");
        }

        entry.setStatus(EntryStatus.PUBLISHED);
        competitionEntryRepository.save(entry);
    }

    @Transactional
    public void rejectEntry(UUID competitionId, UUID entryId) {
        CompetitionEntry entry = competitionEntryRepository.findById(entryId)
                .orElseThrow(() -> new EntryNotFoundException("Entry not found"));

        if (!entry.getCompetition().getId().equals(competitionId)) {
            throw new InvalidEntryStateException("Entry does not belong to this competition");
        }

        entry.setStatus(EntryStatus.REJECTED);
        competitionEntryRepository.save(entry);
    }
}
