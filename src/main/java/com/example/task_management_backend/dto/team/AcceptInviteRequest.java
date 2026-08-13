package com.example.task_management_backend.dto.team;

import jakarta.validation.constraints.NotBlank;

public record AcceptInviteRequest(
		@NotBlank String token
) {
}
