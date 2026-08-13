package com.example.task_management_backend.controller;

import com.example.task_management_backend.dto.user.UpdateProfileRequest;
import com.example.task_management_backend.dto.user.UserResponse;
import com.example.task_management_backend.security.SecurityUtils;
import com.example.task_management_backend.security.UserPrincipal;
import com.example.task_management_backend.service.UserService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/users")
@RequiredArgsConstructor
public class UserController {

	private final UserService userService;

	@GetMapping("/me")
	public UserResponse getMe() {
		UserPrincipal principal = SecurityUtils.currentUser();
		return userService.getMe(principal);
	}

	@PutMapping("/me")
	public UserResponse updateMe(@Valid @RequestBody UpdateProfileRequest request) {
		UserPrincipal principal = SecurityUtils.currentUser();
		return userService.updateMe(principal, request);
	}
}
