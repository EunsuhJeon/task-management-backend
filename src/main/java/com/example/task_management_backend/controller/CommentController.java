package com.example.task_management_backend.controller;

import com.example.task_management_backend.dto.comment.CommentResponse;
import com.example.task_management_backend.dto.comment.CreateCommentRequest;
import com.example.task_management_backend.security.SecurityUtils;
import com.example.task_management_backend.security.UserPrincipal;
import com.example.task_management_backend.service.CommentService;
import jakarta.validation.Valid;
import java.util.List;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/teams/{teamId}/tasks/{taskId}/comments")
@RequiredArgsConstructor
public class CommentController {

	private final CommentService commentService;

	@GetMapping
	public List<CommentResponse> list(@PathVariable Long teamId, @PathVariable Long taskId) {
		UserPrincipal principal = SecurityUtils.currentUser();
		return commentService.listComments(principal, teamId, taskId);
	}

	@PostMapping
	@ResponseStatus(HttpStatus.CREATED)
	public CommentResponse create(
			@PathVariable Long teamId,
			@PathVariable Long taskId,
			@Valid @RequestBody CreateCommentRequest request
	) {
		UserPrincipal principal = SecurityUtils.currentUser();
		return commentService.createComment(principal, teamId, taskId, request);
	}

	@DeleteMapping("/{commentId}")
	@ResponseStatus(HttpStatus.NO_CONTENT)
	public void delete(
			@PathVariable Long teamId,
			@PathVariable Long taskId,
			@PathVariable Long commentId
	) {
		UserPrincipal principal = SecurityUtils.currentUser();
		commentService.deleteComment(principal, teamId, taskId, commentId);
	}
}
