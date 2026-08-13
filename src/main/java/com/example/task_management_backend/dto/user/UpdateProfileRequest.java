package com.example.task_management_backend.dto.user;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;

public record UpdateProfileRequest(
		@NotBlank @Size(min = 1, max = 50) String name
) {
}
