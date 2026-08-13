package com.example.task_management_backend.dto.task;

import com.example.task_management_backend.domain.entity.Task;
import com.example.task_management_backend.domain.enums.TaskStatus;
import com.example.task_management_backend.dto.user.UserResponse;
import java.time.Instant;
import java.time.LocalDate;

public record TaskResponse(
		Long id,
		Long teamId,
		String title,
		String description,
		TaskStatus status,
		UserResponse assignee,
		UserResponse createdBy,
		LocalDate dueDate,
		Instant createdAt,
		Instant updatedAt
) {
	public static TaskResponse from(Task task) {
		return new TaskResponse(
				task.getId(),
				task.getTeam().getId(),
				task.getTitle(),
				task.getDescription(),
				task.getStatus(),
				task.getAssignee() == null ? null : UserResponse.from(task.getAssignee()),
				UserResponse.from(task.getCreatedBy()),
				task.getDueDate(),
				task.getCreatedAt(),
				task.getUpdatedAt()
		);
	}
}
