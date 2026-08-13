package com.example.task_management_backend.service;

import com.example.task_management_backend.domain.entity.Team;
import com.example.task_management_backend.domain.entity.TeamMember;
import com.example.task_management_backend.domain.enums.TeamRole;
import com.example.task_management_backend.domain.repository.TeamMemberRepository;
import com.example.task_management_backend.domain.repository.TeamRepository;
import com.example.task_management_backend.exception.ApiException;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
public class TeamAuthorizationService {

	private final TeamRepository teamRepository;
	private final TeamMemberRepository teamMemberRepository;

	@Transactional(readOnly = true)
	public Team requireTeam(Long teamId) {
		return teamRepository.findByIdWithOwner(teamId)
				.orElseThrow(() -> new ApiException(HttpStatus.NOT_FOUND, "Team not found"));
	}

	@Transactional(readOnly = true)
	public TeamMember requireMember(Long teamId, Long userId) {
		requireTeam(teamId);
		return teamMemberRepository.findByTeamIdAndUserId(teamId, userId)
				.orElseThrow(() -> new ApiException(HttpStatus.FORBIDDEN, "You are not a member of this team"));
	}

	@Transactional(readOnly = true)
	public TeamMember requireAdmin(Long teamId, Long userId) {
		TeamMember member = requireMember(teamId, userId);
		if (member.getRole() != TeamRole.ADMIN) {
			throw new ApiException(HttpStatus.FORBIDDEN, "Admin role required");
		}
		return member;
	}
}
