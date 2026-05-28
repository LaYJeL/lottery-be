package com.game.lottery.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import lombok.Data;

@Data
public class UserEmailUpdateRequest {
    @NotBlank
    @Email
    @Schema(example = "new.email@example.com")
    private String email;
}
