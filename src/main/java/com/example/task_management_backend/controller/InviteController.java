package com.example.task_management_backend.controller;

import com.example.task_management_backend.dto.team.AcceptInviteRequest;
import com.example.task_management_backend.dto.team.TeamResponse;
import com.example.task_management_backend.security.SecurityUtils;
import com.example.task_management_backend.security.UserPrincipal;
import com.example.task_management_backend.service.TeamService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/invites")
@RequiredArgsConstructor
public class InviteController {

	private final TeamService teamService;

	@PostMapping("/accept")
	public TeamResponse accept(@Valid @RequestBody AcceptInviteRequest request) {
		UserPrincipal principal = SecurityUtils.currentUser();
		return teamService.acceptInvite(principal, request.token());
	}

	@PostMapping("/{token}/accept")
	public TeamResponse acceptByPath(@PathVariable String token) {
		UserPrincipal principal = SecurityUtils.currentUser();
		return teamService.acceptInvite(principal, token);
	}
}
