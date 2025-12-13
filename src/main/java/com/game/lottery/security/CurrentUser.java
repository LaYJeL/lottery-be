package com.game.lottery.security;

import java.util.UUID;

public final class CurrentUser {
    private static final ThreadLocal<UUID> USER_ID = new ThreadLocal<>();

    private CurrentUser() {}

    public static void set(UUID userId) { USER_ID.set(userId); }

    public static UUID get() { return USER_ID.get(); }

    public static void clear() { USER_ID.remove(); }
}
