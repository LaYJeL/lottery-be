package com.game.lottery.controller;

import com.game.lottery.dto.CompetitionDto;
import com.game.lottery.dto.CompetitionEntryDto;
import com.game.lottery.security.CurrentUser;
import com.game.lottery.service.CompetitionService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.UUID;

@Slf4j
@RestController
@RequestMapping("/api/v1/competitions")
@RequiredArgsConstructor
public class CompetitionController {

    private final CompetitionService competitionService;

    @GetMapping
    public ResponseEntity<org.springframework.data.domain.Page<CompetitionDto>> getActiveCompetitions(
            @org.springframework.data.web.PageableDefault(size = 6, sort = "createdAt", direction = org.springframework.data.domain.Sort.Direction.DESC) org.springframework.data.domain.Pageable pageable) {
        UUID userId = CurrentUser.get();
        log.debug("User {} requested active competitions", userId);
        return ResponseEntity.ok(competitionService.getActiveCompetitions(userId, pageable));
    }

    @GetMapping("/my-entries")
    public ResponseEntity<List<CompetitionEntryDto>> getMyEntries() {
        UUID userId = CurrentUser.get();
        log.debug("User {} requested their entries", userId);
        return ResponseEntity.ok(competitionService.getMyEntries(userId));
    }

    @GetMapping("/{id}")
    public ResponseEntity<CompetitionDto> getCompetitionById(@PathVariable UUID id) {
        UUID userId = CurrentUser.get();
        log.debug("User {} requested competition details for {}", userId, id);
        return ResponseEntity.ok(competitionService.getCompetitionById(userId, id));
    }

    @PostMapping("/{id}/join")
    public ResponseEntity<Void> joinCompetition(@PathVariable UUID id) {
        UUID userId = CurrentUser.get();
        log.info("User {} joining competition {}", userId, id);
        competitionService.joinCompetition(userId, id);
        return ResponseEntity.ok().build();
    }

    @PostMapping("/{id}/submit")
    public ResponseEntity<Void> submitEntry(@PathVariable UUID id, @Valid @RequestBody SubmitEntryRequest request) {
        UUID userId = CurrentUser.get();
        log.info("User {} submitting entry for competition {}", userId, id);
        competitionService.submitEntry(userId, id, request.getContent());
        return ResponseEntity.ok().build();
    }

    // Inner DTO Class
    @lombok.Data
    public static class SubmitEntryRequest {
        @jakarta.validation.constraints.NotBlank(message = "Content cannot be empty")
        @jakarta.validation.constraints.Size(max = 1000, message = "Content cannot exceed 1000 characters")
        private String content;
    }
}
