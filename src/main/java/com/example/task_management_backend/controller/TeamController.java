package com.example.task_management_backend.controller;

import com.example.task_management_backend.dto.team.CreateTeamRequest;
import com.example.task_management_backend.dto.team.TeamInviteResponse;
import com.example.task_management_backend.dto.team.TeamMemberResponse;
import com.example.task_management_backend.dto.team.TeamResponse;
import com.example.task_management_backend.security.SecurityUtils;
import com.example.task_management_backend.security.UserPrincipal;
import com.example.task_management_backend.service.TeamService;
import jakarta.validation.Valid;
import java.util.List;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/teams")
@RequiredArgsConstructor
public class TeamController {

	private final TeamService teamService;

	@PostMapping
	@ResponseStatus(HttpStatus.CREATED)
	public TeamResponse create(@Valid @RequestBody CreateTeamRequest request) {
		UserPrincipal principal = SecurityUtils.currentUser();
		return teamService.createTeam(principal, request);
	}

	@GetMapping
	public List<TeamResponse> listMine() {
		UserPrincipal principal = SecurityUtils.currentUser();
		return teamService.listMyTeams(principal);
	}

	@GetMapping("/{teamId}")
	public TeamResponse get(@PathVariable Long teamId) {
		UserPrincipal principal = SecurityUtils.currentUser();
		return teamService.getTeam(principal, teamId);
	}

	@GetMapping("/{teamId}/members")
	public List<TeamMemberResponse> listMembers(@PathVariable Long teamId) {
		UserPrincipal principal = SecurityUtils.currentUser();
		return teamService.listMembers(principal, teamId);
	}

	@PostMapping("/{teamId}/invites")
	@ResponseStatus(HttpStatus.CREATED)
	public TeamInviteResponse createInvite(@PathVariable Long teamId) {
		UserPrincipal principal = SecurityUtils.currentUser();
		return teamService.createInvite(principal, teamId);
	}
}
