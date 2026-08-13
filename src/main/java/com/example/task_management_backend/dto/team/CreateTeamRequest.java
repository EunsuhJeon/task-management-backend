package com.example.task_management_backend.dto.team;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;

public record CreateTeamRequest(
		@NotBlank @Size(max = 100) String name,
		@Size(max = 500) String description
) {
}
