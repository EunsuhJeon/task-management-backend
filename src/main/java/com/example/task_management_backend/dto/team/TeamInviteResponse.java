package com.example.task_management_backend.dto.team;

import java.time.Instant;

public record TeamInviteResponse(
		String token,
		String invitePath,
		Instant expiresAt
) {
	public static TeamInviteResponse of(String token, Instant expiresAt) {
		return new TeamInviteResponse(token, "/api/invites/" + token + "/accept", expiresAt);
	}
}
