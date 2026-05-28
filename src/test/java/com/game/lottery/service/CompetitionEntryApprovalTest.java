package com.game.lottery.service;

import com.game.lottery.dto.CompetitionDto;
import com.game.lottery.enums.CompetitionStatus;
import com.game.lottery.enums.CompetitionType;
import com.game.lottery.enums.EntryStatus;
import com.game.lottery.model.Competition;
import com.game.lottery.model.CompetitionEntry;
import com.game.lottery.model.User;
import com.game.lottery.model.UserProfile;
import com.game.lottery.repository.CompetitionEntryRepository;
import com.game.lottery.repository.CompetitionRepository;
import com.game.lottery.repository.UserRepository;
import com.game.lottery.enums.AuthenticationProvider;
import com.game.lottery.enums.VerificationLevel;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.test.context.DynamicPropertyRegistry;
import org.springframework.test.context.DynamicPropertySource;
import org.testcontainers.containers.PostgreSQLContainer;
import org.testcontainers.junit.jupiter.Container;
import org.testcontainers.junit.jupiter.Testcontainers;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.doNothing;

@SpringBootTest
@Testcontainers
public class CompetitionEntryApprovalTest {

    @Container
    static PostgreSQLContainer<?> postgres = new PostgreSQLContainer<>("postgres:15-alpine");

    @DynamicPropertySource
    static void configureProperties(DynamicPropertyRegistry registry) {
        registry.add("spring.datasource.url", postgres::getJdbcUrl);
        registry.add("spring.datasource.username", postgres::getUsername);
        registry.add("spring.datasource.password", postgres::getPassword);
    }

    @Autowired
    private CompetitionService competitionService;

    @Autowired
    private CompetitionRepository competitionRepository;

    @Autowired
    private CompetitionEntryRepository competitionEntryRepository;

    @Autowired
    private UserRepository userRepository;

    @MockBean
    private WalletService walletService;

    private User user;

    @BeforeEach
    void setUp() {
        competitionEntryRepository.deleteAll();
        competitionRepository.deleteAll();
        userRepository.deleteAll();

        UserProfile profile = new UserProfile();
        profile.setEmail("test@test.com");
        profile.setVerificationLevel(VerificationLevel.NEW);

        user = User.builder()
                .profile(profile)
                .keycloakSub("test-sub-" + UUID.randomUUID())
                .authProvider(AuthenticationProvider.LOCAL)
                .accountStatus(com.game.lottery.enums.AccountStatus.ACTIVE)
                .build();

        user = userRepository.save(user);

        doNothing().when(walletService).spendForCompetition(any(), any(), any());
    }

    @Test
    void submitEntry_shouldBePublished_whenApprovalNotRequired() {
        Competition competition = createCompetition(false);

        competitionService.joinCompetition(user.getId(), competition.getId());
        competitionService.submitEntry(user.getId(), competition.getId(), "My Entry");

        CompetitionEntry entry = competitionEntryRepository
                .findByCompetition_IdAndUser_UserId(competition.getId(), user.getId()).orElseThrow();
        assertThat(entry.getStatus()).isEqualTo(EntryStatus.PUBLISHED);
    }

    @Test
    void submitEntry_shouldBeUnderReview_whenApprovalRequired() {
        Competition competition = createCompetition(true);

        competitionService.joinCompetition(user.getId(), competition.getId());
        competitionService.submitEntry(user.getId(), competition.getId(), "My Entry");

        CompetitionEntry entry = competitionEntryRepository
                .findByCompetition_IdAndUser_UserId(competition.getId(), user.getId()).orElseThrow();
        assertThat(entry.getStatus()).isEqualTo(EntryStatus.UNDER_REVIEW);
    }

    @Test
    void approveEntry_shouldChangeStatusToPublished() {
        Competition competition = createCompetition(true);
        competitionService.joinCompetition(user.getId(), competition.getId());
        competitionService.submitEntry(user.getId(), competition.getId(), "My Entry");

        CompetitionEntry entry = competitionEntryRepository
                .findByCompetition_IdAndUser_UserId(competition.getId(), user.getId()).orElseThrow();

        competitionService.approveEntry(competition.getId(), entry.getId());

        entry = competitionEntryRepository.findById(entry.getId()).orElseThrow();
        assertThat(entry.getStatus()).isEqualTo(EntryStatus.PUBLISHED);
    }

    @Test
    void rejectEntry_shouldChangeStatusToRejected() {
        Competition competition = createCompetition(true);
        competitionService.joinCompetition(user.getId(), competition.getId());
        competitionService.submitEntry(user.getId(), competition.getId(), "My Entry");

        CompetitionEntry entry = competitionEntryRepository
                .findByCompetition_IdAndUser_UserId(competition.getId(), user.getId()).orElseThrow();

        competitionService.rejectEntry(competition.getId(), entry.getId());

        entry = competitionEntryRepository.findById(entry.getId()).orElseThrow();
        assertThat(entry.getStatus()).isEqualTo(EntryStatus.REJECTED);
    }

    private Competition createCompetition(boolean requiresApproval) {
        Competition competition = Competition.builder()
                .title("Test Competition")
                .type(CompetitionType.OTHER)
                .entryFee(BigDecimal.TEN)
                .status(CompetitionStatus.ACTIVE)
                .requiresApproval(requiresApproval)
                .startTime(LocalDateTime.now())
                .endTime(LocalDateTime.now().plusDays(1))
                .build();
        return competitionRepository.save(competition);
    }
}
