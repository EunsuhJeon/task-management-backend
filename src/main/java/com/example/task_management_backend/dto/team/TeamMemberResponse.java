package com.example.task_management_backend.dto.team;

import com.example.task_management_backend.domain.entity.TeamMember;
import com.example.task_management_backend.domain.enums.TeamRole;
import com.example.task_management_backend.dto.user.UserResponse;
import java.time.Instant;

public record TeamMemberResponse(
		Long id,
		UserResponse user,
		TeamRole role,
		Instant joinedAt
) {
	public static TeamMemberResponse from(TeamMember member) {
		return new TeamMemberResponse(
				member.getId(),
				UserResponse.from(member.getUser()),
				member.getRole(),
				member.getJoinedAt()
		);
	}
}
