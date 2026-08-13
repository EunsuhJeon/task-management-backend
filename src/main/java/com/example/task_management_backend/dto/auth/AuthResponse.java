package com.example.task_management_backend.dto.auth;

import com.example.task_management_backend.dto.user.UserResponse;

public record AuthResponse(
		String accessToken,
		String tokenType,
		UserResponse user
) {
	public static AuthResponse bearer(String accessToken, UserResponse user) {
		return new AuthResponse(accessToken, "Bearer", user);
	}
}
