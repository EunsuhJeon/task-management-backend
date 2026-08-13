package com.example.task_management_backend.dto.comment;

import com.example.task_management_backend.domain.entity.Comment;
import com.example.task_management_backend.dto.user.UserResponse;
import java.time.Instant;

public record CommentResponse(
		Long id,
		Long taskId,
		String content,
		UserResponse author,
		Instant createdAt
) {
	public static CommentResponse from(Comment comment) {
		return new CommentResponse(
				comment.getId(),
				comment.getTask().getId(),
				comment.getContent(),
				UserResponse.from(comment.getAuthor()),
				comment.getCreatedAt()
		);
	}
}
