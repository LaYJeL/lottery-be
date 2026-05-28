package com.game.lottery.controller;

import com.game.lottery.dto.CompetitionDto;
import com.game.lottery.enums.CompetitionStatus;
import com.game.lottery.service.CompetitionService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.UUID;

@RestController
@RequestMapping("/api/v1/admin/competitions")
@RequiredArgsConstructor
@PreAuthorize("hasRole('ADMIN')")
public class AdminCompetitionController {

    private final CompetitionService competitionService;

    @PostMapping
    public ResponseEntity<CompetitionDto> createCompetition(@RequestBody CompetitionDto dto) {
        return ResponseEntity.ok(competitionService.createCompetition(dto));
    }

    @GetMapping
    public ResponseEntity<java.util.List<CompetitionDto>> getAllCompetitions() {
        return ResponseEntity.ok(competitionService.getAllCompetitions());
    }

    @PutMapping("/{id}/status")
    public ResponseEntity<CompetitionDto> updateStatus(@PathVariable UUID id,
            @RequestBody UpdateStatusRequest request) {
        return ResponseEntity.ok(competitionService.updateCompetitionStatus(id, request.getStatus()));
    }

    // Inner DTO Class
    @lombok.Data
    public static class UpdateStatusRequest {
        private CompetitionStatus status;
    }

    @PutMapping("/{id}/entries/{entryId}/approve")
    public ResponseEntity<Void> approveEntry(@PathVariable UUID id, @PathVariable UUID entryId) {
        competitionService.approveEntry(id, entryId);
        return ResponseEntity.ok().build();
    }

    @PutMapping("/{id}/entries/{entryId}/reject")
    public ResponseEntity<Void> rejectEntry(@PathVariable UUID id, @PathVariable UUID entryId) {
        competitionService.rejectEntry(id, entryId);
        return ResponseEntity.ok().build();
    }
}
