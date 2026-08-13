package com.example.task_management_backend.controller;

import com.example.task_management_backend.dto.task.CreateTaskRequest;
import com.example.task_management_backend.dto.task.TaskResponse;
import com.example.task_management_backend.dto.task.UpdateTaskRequest;
import com.example.task_management_backend.security.SecurityUtils;
import com.example.task_management_backend.security.UserPrincipal;
import com.example.task_management_backend.service.TaskService;
import jakarta.validation.Valid;
import java.util.List;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/teams/{teamId}/tasks")
@RequiredArgsConstructor
public class TaskController {

	private final TaskService taskService;

	@GetMapping
	public List<TaskResponse> list(@PathVariable Long teamId) {
		UserPrincipal principal = SecurityUtils.currentUser();
		return taskService.listTasks(principal, teamId);
	}

	@GetMapping("/{taskId}")
	public TaskResponse get(@PathVariable Long teamId, @PathVariable Long taskId) {
		UserPrincipal principal = SecurityUtils.currentUser();
		return taskService.getTask(principal, teamId, taskId);
	}

	@PostMapping
	@ResponseStatus(HttpStatus.CREATED)
	public TaskResponse create(@PathVariable Long teamId, @Valid @RequestBody CreateTaskRequest request) {
		UserPrincipal principal = SecurityUtils.currentUser();
		return taskService.createTask(principal, teamId, request);
	}

	@PutMapping("/{taskId}")
	public TaskResponse update(
			@PathVariable Long teamId,
			@PathVariable Long taskId,
			@Valid @RequestBody UpdateTaskRequest request
	) {
		UserPrincipal principal = SecurityUtils.currentUser();
		return taskService.updateTask(principal, teamId, taskId, request);
	}

	@DeleteMapping("/{taskId}")
	@ResponseStatus(HttpStatus.NO_CONTENT)
	public void delete(@PathVariable Long teamId, @PathVariable Long taskId) {
		UserPrincipal principal = SecurityUtils.currentUser();
		taskService.deleteTask(principal, teamId, taskId);
	}
}
