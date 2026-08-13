package com.example.task_management_backend.security;

import tools.jackson.databind.ObjectMapper;
import com.example.task_management_backend.dto.common.ErrorResponse;
import jakarta.servlet.http.HttpServletResponse;
import java.io.IOException;
import lombok.RequiredArgsConstructor;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.HttpMethod;
import org.springframework.http.MediaType;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.dao.DaoAuthenticationProvider;
import org.springframework.security.config.Customizer;
import org.springframework.security.config.annotation.authentication.configuration.AuthenticationConfiguration;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configurers.AbstractHttpConfigurer;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.AuthenticationEntryPoint;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.access.AccessDeniedHandler;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;

@Configuration
@EnableWebSecurity
@RequiredArgsConstructor
public class SecurityConfig {

	private final JwtAuthenticationFilter jwtAuthenticationFilter;
	private final CustomUserDetailsService userDetailsService;
	private final ObjectMapper objectMapper;

	@Bean
	public SecurityFilterChain securityFilterChain(HttpSecurity http) throws Exception {
		http
				.csrf(AbstractHttpConfigurer::disable)
				.cors(Customizer.withDefaults())
				.sessionManagement(session -> session.sessionCreationPolicy(SessionCreationPolicy.STATELESS))
				.exceptionHandling(ex -> ex
						.authenticationEntryPoint(authenticationEntryPoint())
						.accessDeniedHandler(accessDeniedHandler())
				)
				.authorizeHttpRequests(auth -> auth
						.requestMatchers(HttpMethod.POST, "/api/auth/signup", "/api/auth/login").permitAll()
						.requestMatchers("/error").permitAll()
						.anyRequest().authenticated()
				)
				.authenticationProvider(authenticationProvider())
				.addFilterBefore(jwtAuthenticationFilter, UsernamePasswordAuthenticationFilter.class);

		return http.build();
	}

	@Bean
	public DaoAuthenticationProvider authenticationProvider() {
		DaoAuthenticationProvider provider = new DaoAuthenticationProvider(userDetailsService);
		provider.setPasswordEncoder(passwordEncoder());
		return provider;
	}

	@Bean
	public AuthenticationManager authenticationManager(AuthenticationConfiguration configuration) throws Exception {
		return configuration.getAuthenticationManager();
	}

	@Bean
	public PasswordEncoder passwordEncoder() {
		return new BCryptPasswordEncoder();
	}

	@Bean
	public AuthenticationEntryPoint authenticationEntryPoint() {
		return (request, response, authException) ->
				writeError(response, HttpServletResponse.SC_UNAUTHORIZED, "Unauthorized", "Authentication required", request.getRequestURI());
	}

	@Bean
	public AccessDeniedHandler accessDeniedHandler() {
		return (request, response, accessDeniedException) ->
				writeError(response, HttpServletResponse.SC_FORBIDDEN, "Forbidden", "Access denied", request.getRequestURI());
	}

	private void writeError(
			HttpServletResponse response,
			int status,
			String error,
			String message,
			String path
	) throws IOException {
		response.setStatus(status);
		response.setContentType(MediaType.APPLICATION_JSON_VALUE);
		objectMapper.writeValue(response.getOutputStream(), ErrorResponse.of(status, error, message, path));
	}
}
