package com.example.task_management_backend.service;

import com.example.task_management_backend.domain.entity.Team;
import com.example.task_management_backend.domain.entity.TeamInvite;
import com.example.task_management_backend.domain.entity.TeamMember;
import com.example.task_management_backend.domain.entity.User;
import com.example.task_management_backend.domain.enums.TeamRole;
import com.example.task_management_backend.domain.repository.TeamInviteRepository;
import com.example.task_management_backend.domain.repository.TeamMemberRepository;
import com.example.task_management_backend.domain.repository.TeamRepository;
import com.example.task_management_backend.domain.repository.UserRepository;
import com.example.task_management_backend.dto.team.CreateTeamRequest;
import com.example.task_management_backend.dto.team.TeamInviteResponse;
import com.example.task_management_backend.dto.team.TeamMemberResponse;
import com.example.task_management_backend.dto.team.TeamResponse;
import com.example.task_management_backend.exception.ApiException;
import com.example.task_management_backend.security.UserPrincipal;
import java.time.Instant;
import java.time.temporal.ChronoUnit;
import java.util.List;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
public class TeamService {

	private static final long INVITE_VALID_DAYS = 7;

	private final TeamRepository teamRepository;
	private final TeamMemberRepository teamMemberRepository;
	private final TeamInviteRepository teamInviteRepository;
	private final UserRepository userRepository;
	private final TeamAuthorizationService teamAuthorizationService;

	@Transactional
	public TeamResponse createTeam(UserPrincipal principal, CreateTeamRequest request) {
		User owner = findUser(principal.getId());

		Team team = Team.builder()
				.name(request.name().trim())
				.description(trimToNull(request.description()))
				.owner(owner)
				.build();
		Team saved = teamRepository.save(team);

		TeamMember membership = TeamMember.builder()
				.team(saved)
				.user(owner)
				.role(TeamRole.ADMIN)
				.build();
		teamMemberRepository.save(membership);

		return TeamResponse.from(saved, TeamRole.ADMIN);
	}

	@Transactional(readOnly = true)
	public List<TeamResponse> listMyTeams(UserPrincipal principal) {
		return teamMemberRepository.findByUserIdWithTeam(principal.getId()).stream()
				.map(TeamResponse::from)
				.toList();
	}

	@Transactional(readOnly = true)
	public TeamResponse getTeam(UserPrincipal principal, Long teamId) {
		TeamMember membership = teamAuthorizationService.requireMember(teamId, principal.getId());
		Team team = teamAuthorizationService.requireTeam(teamId);
		return TeamResponse.from(team, membership.getRole());
	}

	@Transactional(readOnly = true)
	public List<TeamMemberResponse> listMembers(UserPrincipal principal, Long teamId) {
		teamAuthorizationService.requireMember(teamId, principal.getId());
		return teamMemberRepository.findByTeamIdWithUser(teamId).stream()
				.map(TeamMemberResponse::from)
				.toList();
	}

	@Transactional
	public TeamInviteResponse createInvite(UserPrincipal principal, Long teamId) {
		teamAuthorizationService.requireAdmin(teamId, principal.getId());
		Team team = teamAuthorizationService.requireTeam(teamId);
		User creator = findUser(principal.getId());

		TeamInvite invite = TeamInvite.builder()
				.team(team)
				.createdBy(creator)
				.expiresAt(Instant.now().plus(INVITE_VALID_DAYS, ChronoUnit.DAYS))
				.build();
		TeamInvite saved = teamInviteRepository.save(invite);
		return TeamInviteResponse.of(saved.getToken(), saved.getExpiresAt());
	}

	@Transactional
	public TeamResponse acceptInvite(UserPrincipal principal, String token) {
		TeamInvite invite = teamInviteRepository.findByTokenWithTeam(token)
				.orElseThrow(() -> new ApiException(HttpStatus.NOT_FOUND, "Invite not found"));

		if (!invite.isUsable()) {
			throw new ApiException(HttpStatus.BAD_REQUEST, "Invite is expired or revoked");
		}

		User user = findUser(principal.getId());
		Team team = invite.getTeam();

		if (teamMemberRepository.existsByTeamIdAndUserId(team.getId(), user.getId())) {
			TeamMember existing = teamAuthorizationService.requireMember(team.getId(), user.getId());
			return TeamResponse.from(team, existing.getRole());
		}

		TeamMember membership = TeamMember.builder()
				.team(team)
				.user(user)
				.role(TeamRole.MEMBER)
				.build();
		teamMemberRepository.save(membership);

		return TeamResponse.from(team, TeamRole.MEMBER);
	}

	private User findUser(Long id) {
		return userRepository.findById(id)
				.orElseThrow(() -> new ApiException(HttpStatus.NOT_FOUND, "User not found"));
	}

	private String trimToNull(String value) {
		if (value == null) {
			return null;
		}
		String trimmed = value.trim();
		return trimmed.isEmpty() ? null : trimmed;
	}
}
