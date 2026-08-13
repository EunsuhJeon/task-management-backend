package com.example.task_management_backend.exception;

import com.example.task_management_backend.dto.common.ErrorResponse;
import jakarta.servlet.http.HttpServletRequest;
import java.util.List;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.core.AuthenticationException;
import org.springframework.validation.FieldError;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

@RestControllerAdvice
public class GlobalExceptionHandler {

	@ExceptionHandler(ApiException.class)
	public ResponseEntity<ErrorResponse> handleApiException(ApiException ex, HttpServletRequest request) {
		return build(ex.getStatus(), ex.getStatus().getReasonPhrase(), ex.getMessage(), request.getRequestURI());
	}

	@ExceptionHandler(MethodArgumentNotValidException.class)
	public ResponseEntity<ErrorResponse> handleValidation(
			MethodArgumentNotValidException ex,
			HttpServletRequest request
	) {
		List<ErrorResponse.FieldErrorDetail> fieldErrors = ex.getBindingResult().getFieldErrors().stream()
				.map(this::toFieldError)
				.toList();

		ErrorResponse body = ErrorResponse.of(
				HttpStatus.BAD_REQUEST.value(),
				"Bad Request",
				"Validation failed",
				request.getRequestURI(),
				fieldErrors
		);
		return ResponseEntity.badRequest().body(body);
	}

	@ExceptionHandler({BadCredentialsException.class, AuthenticationException.class})
	public ResponseEntity<ErrorResponse> handleAuth(RuntimeException ex, HttpServletRequest request) {
		return build(HttpStatus.UNAUTHORIZED, "Unauthorized", "Invalid email or password", request.getRequestURI());
	}

	@ExceptionHandler(AccessDeniedException.class)
	public ResponseEntity<ErrorResponse> handleAccessDenied(AccessDeniedException ex, HttpServletRequest request) {
		return build(HttpStatus.FORBIDDEN, "Forbidden", "Access denied", request.getRequestURI());
	}

	@ExceptionHandler(Exception.class)
	public ResponseEntity<ErrorResponse> handleUnexpected(Exception ex, HttpServletRequest request) {
		return build(
				HttpStatus.INTERNAL_SERVER_ERROR,
				"Internal Server Error",
				"Unexpected server error",
				request.getRequestURI()
		);
	}

	private ErrorResponse.FieldErrorDetail toFieldError(FieldError error) {
		return new ErrorResponse.FieldErrorDetail(error.getField(), error.getDefaultMessage());
	}

	private ResponseEntity<ErrorResponse> build(HttpStatus status, String error, String message, String path) {
		return ResponseEntity.status(status).body(ErrorResponse.of(status.value(), error, message, path));
	}
}
