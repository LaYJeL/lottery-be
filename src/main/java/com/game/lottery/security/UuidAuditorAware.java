package com.game.lottery.security;

import org.springframework.data.domain.AuditorAware;

import java.util.Optional;
import java.util.UUID;

public class UuidAuditorAware implements AuditorAware<UUID> {
    @Override
    public Optional<UUID> getCurrentAuditor() {
        return Optional.ofNullable(CurrentUser.get());
    }
}
