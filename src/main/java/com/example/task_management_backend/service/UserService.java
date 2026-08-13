package com.example.task_management_backend.service;

import com.example.task_management_backend.domain.entity.User;
import com.example.task_management_backend.domain.repository.UserRepository;
import com.example.task_management_backend.dto.user.UpdateProfileRequest;
import com.example.task_management_backend.dto.user.UserResponse;
import com.example.task_management_backend.exception.ApiException;
import com.example.task_management_backend.security.UserPrincipal;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
public class UserService {

	private final UserRepository userRepository;

	@Transactional(readOnly = true)
	public UserResponse getMe(UserPrincipal principal) {
		return UserResponse.from(findUser(principal.getId()));
	}

	@Transactional
	public UserResponse updateMe(UserPrincipal principal, UpdateProfileRequest request) {
		User user = findUser(principal.getId());
		user.updateProfile(request.name().trim());
		return UserResponse.from(user);
	}

	private User findUser(Long id) {
		return userRepository.findById(id)
				.orElseThrow(() -> new ApiException(HttpStatus.NOT_FOUND, "User not found"));
	}
}
