package com.example.task_management_backend.service;

import com.example.task_management_backend.domain.entity.Task;
import com.example.task_management_backend.domain.entity.Team;
import com.example.task_management_backend.domain.entity.User;
import com.example.task_management_backend.domain.enums.TaskStatus;
import com.example.task_management_backend.domain.repository.TaskRepository;
import com.example.task_management_backend.domain.repository.TeamMemberRepository;
import com.example.task_management_backend.domain.repository.UserRepository;
import com.example.task_management_backend.dto.task.CreateTaskRequest;
import com.example.task_management_backend.dto.task.TaskResponse;
import com.example.task_management_backend.dto.task.UpdateTaskRequest;
import com.example.task_management_backend.exception.ApiException;
import com.example.task_management_backend.security.UserPrincipal;
import java.util.List;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
public class TaskService {

	private final TaskRepository taskRepository;
	private final UserRepository userRepository;
	private final TeamMemberRepository teamMemberRepository;
	private final TeamAuthorizationService teamAuthorizationService;

	@Transactional(readOnly = true)
	public List<TaskResponse> listTasks(UserPrincipal principal, Long teamId) {
		teamAuthorizationService.requireMember(teamId, principal.getId());
		return taskRepository.findByTeamIdWithDetails(teamId).stream()
				.map(TaskResponse::from)
				.toList();
	}

	@Transactional(readOnly = true)
	public TaskResponse getTask(UserPrincipal principal, Long teamId, Long taskId) {
		teamAuthorizationService.requireMember(teamId, principal.getId());
		return TaskResponse.from(findTask(teamId, taskId));
	}

	@Transactional
	public TaskResponse createTask(UserPrincipal principal, Long teamId, CreateTaskRequest request) {
		teamAuthorizationService.requireMember(teamId, principal.getId());
		Team team = teamAuthorizationService.requireTeam(teamId);
		User creator = findUser(principal.getId());
		User assignee = resolveAssignee(teamId, request.assigneeId());

		Task task = Task.builder()
				.title(request.title().trim())
				.description(trimToNull(request.description()))
				.status(request.status() == null ? TaskStatus.TODO : request.status())
				.team(team)
				.assignee(assignee)
				.createdBy(creator)
				.dueDate(request.dueDate())
				.build();

		return TaskResponse.from(taskRepository.save(task));
	}

	@Transactional
	public TaskResponse updateTask(UserPrincipal principal, Long teamId, Long taskId, UpdateTaskRequest request) {
		teamAuthorizationService.requireMember(teamId, principal.getId());
		Task task = findTask(teamId, taskId);
		User assignee = resolveAssignee(teamId, request.assigneeId());

		task.update(
				request.title().trim(),
				trimToNull(request.description()),
				request.status(),
				assignee,
				request.dueDate()
		);

		return TaskResponse.from(task);
	}

	@Transactional
	public void deleteTask(UserPrincipal principal, Long teamId, Long taskId) {
		teamAuthorizationService.requireAdmin(teamId, principal.getId());
		Task task = findTask(teamId, taskId);
		taskRepository.delete(task);
	}

	private Task findTask(Long teamId, Long taskId) {
		return taskRepository.findByIdAndTeamIdWithDetails(taskId, teamId)
				.orElseThrow(() -> new ApiException(HttpStatus.NOT_FOUND, "Task not found"));
	}

	private User resolveAssignee(Long teamId, Long assigneeId) {
		if (assigneeId == null) {
			return null;
		}
		if (!teamMemberRepository.existsByTeamIdAndUserId(teamId, assigneeId)) {
			throw new ApiException(HttpStatus.BAD_REQUEST, "Assignee must be a team member");
		}
		return findUser(assigneeId);
	}

	private User findUser(Long id) {
		return userRepository.findById(id)
				.orElseThrow(() -> new ApiException(HttpStatus.NOT_FOUND, "User not found"));
	}

	private String trimToNull(String value) {
		if (value == null) {
			return null;
		}
		String trimmed = value.trim();
		return trimmed.isEmpty() ? null : trimmed;
	}
}
