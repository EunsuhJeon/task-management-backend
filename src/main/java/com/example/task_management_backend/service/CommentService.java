package com.example.task_management_backend.service;

import com.example.task_management_backend.domain.entity.Comment;
import com.example.task_management_backend.domain.entity.Task;
import com.example.task_management_backend.domain.entity.User;
import com.example.task_management_backend.domain.enums.TeamRole;
import com.example.task_management_backend.domain.repository.CommentRepository;
import com.example.task_management_backend.domain.repository.TaskRepository;
import com.example.task_management_backend.domain.repository.UserRepository;
import com.example.task_management_backend.dto.comment.CommentResponse;
import com.example.task_management_backend.dto.comment.CreateCommentRequest;
import com.example.task_management_backend.exception.ApiException;
import com.example.task_management_backend.security.UserPrincipal;
import java.util.List;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
public class CommentService {

	private final CommentRepository commentRepository;
	private final TaskRepository taskRepository;
	private final UserRepository userRepository;
	private final TeamAuthorizationService teamAuthorizationService;

	@Transactional(readOnly = true)
	public List<CommentResponse> listComments(UserPrincipal principal, Long teamId, Long taskId) {
		teamAuthorizationService.requireMember(teamId, principal.getId());
		ensureTaskExists(teamId, taskId);
		return commentRepository.findByTeamIdAndTaskIdWithAuthor(teamId, taskId).stream()
				.map(CommentResponse::from)
				.toList();
	}

	@Transactional
	public CommentResponse createComment(
			UserPrincipal principal,
			Long teamId,
			Long taskId,
			CreateCommentRequest request
	) {
		teamAuthorizationService.requireMember(teamId, principal.getId());
		Task task = ensureTaskExists(teamId, taskId);
		User author = userRepository.findById(principal.getId())
				.orElseThrow(() -> new ApiException(HttpStatus.NOT_FOUND, "User not found"));

		Comment comment = Comment.builder()
				.content(request.content().trim())
				.task(task)
				.author(author)
				.build();

		return CommentResponse.from(commentRepository.save(comment));
	}

	@Transactional
	public void deleteComment(UserPrincipal principal, Long teamId, Long taskId, Long commentId) {
		var membership = teamAuthorizationService.requireMember(teamId, principal.getId());
		Comment comment = commentRepository.findByIdAndTaskIdAndTeamId(commentId, taskId, teamId)
				.orElseThrow(() -> new ApiException(HttpStatus.NOT_FOUND, "Comment not found"));

		boolean isAuthor = comment.getAuthor().getId().equals(principal.getId());
		boolean isAdmin = membership.getRole() == TeamRole.ADMIN;
		if (!isAuthor && !isAdmin) {
			throw new ApiException(HttpStatus.FORBIDDEN, "Only the author or an admin can delete this comment");
		}

		commentRepository.delete(comment);
	}

	private Task ensureTaskExists(Long teamId, Long taskId) {
		return taskRepository.findByIdAndTeamIdWithDetails(taskId, teamId)
				.orElseThrow(() -> new ApiException(HttpStatus.NOT_FOUND, "Task not found"));
	}
}
