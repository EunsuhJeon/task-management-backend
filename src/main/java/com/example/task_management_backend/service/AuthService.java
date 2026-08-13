package com.example.task_management_backend.service;

import com.example.task_management_backend.domain.entity.User;
import com.example.task_management_backend.domain.repository.UserRepository;
import com.example.task_management_backend.dto.auth.AuthResponse;
import com.example.task_management_backend.dto.auth.LoginRequest;
import com.example.task_management_backend.dto.auth.SignupRequest;
import com.example.task_management_backend.dto.user.UserResponse;
import com.example.task_management_backend.exception.ApiException;
import com.example.task_management_backend.security.JwtService;
import com.example.task_management_backend.security.UserPrincipal;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
public class AuthService {

	private final UserRepository userRepository;
	private final PasswordEncoder passwordEncoder;
	private final JwtService jwtService;
	private final AuthenticationManager authenticationManager;

	@Transactional
	public AuthResponse signup(SignupRequest request) {
		if (userRepository.existsByEmail(request.email())) {
			throw new ApiException(HttpStatus.CONFLICT, "Email already registered");
		}

		User user = User.builder()
				.email(request.email().trim().toLowerCase())
				.password(passwordEncoder.encode(request.password()))
				.name(request.name().trim())
				.build();

		User saved = userRepository.save(user);
		UserPrincipal principal = new UserPrincipal(saved);
		String token = jwtService.generateToken(principal);

		return AuthResponse.bearer(token, UserResponse.from(saved));
	}

	@Transactional(readOnly = true)
	public AuthResponse login(LoginRequest request) {
		authenticationManager.authenticate(
				new UsernamePasswordAuthenticationToken(
						request.email().trim().toLowerCase(),
						request.password()
				)
		);

		User user = userRepository.findByEmail(request.email().trim().toLowerCase())
				.orElseThrow(() -> new ApiException(HttpStatus.UNAUTHORIZED, "Invalid email or password"));

		UserPrincipal principal = new UserPrincipal(user);
		String token = jwtService.generateToken(principal);

		return AuthResponse.bearer(token, UserResponse.from(user));
	}
}
