package com.example.task_management_backend.dto.team;

import com.example.task_management_backend.domain.entity.Team;
import com.example.task_management_backend.domain.entity.TeamMember;
import com.example.task_management_backend.domain.enums.TeamRole;
import com.example.task_management_backend.dto.user.UserResponse;
import java.time.Instant;

public record TeamResponse(
		Long id,
		String name,
		String description,
		UserResponse owner,
		TeamRole myRole,
		Instant createdAt
) {
	public static TeamResponse from(Team team, TeamRole myRole) {
		return new TeamResponse(
				team.getId(),
				team.getName(),
				team.getDescription(),
				UserResponse.from(team.getOwner()),
				myRole,
				team.getCreatedAt()
		);
	}

	public static TeamResponse from(TeamMember membership) {
		return from(membership.getTeam(), membership.getRole());
	}
}
