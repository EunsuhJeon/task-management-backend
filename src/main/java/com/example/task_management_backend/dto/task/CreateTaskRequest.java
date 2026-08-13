package com.example.task_management_backend.dto.task;

import com.example.task_management_backend.domain.enums.TaskStatus;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import java.time.LocalDate;

public record CreateTaskRequest(
		@NotBlank @Size(max = 200) String title,
		@Size(max = 2000) String description,
		TaskStatus status,
		Long assigneeId,
		LocalDate dueDate
) {
}
