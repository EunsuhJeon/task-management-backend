package com.example.task_management_backend.domain.entity;

import com.example.task_management_backend.domain.enums.TaskStatus;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.FetchType;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.PrePersist;
import jakarta.persistence.PreUpdate;
import jakarta.persistence.Table;
import java.time.Instant;
import java.time.LocalDate;
import lombok.AccessLevel;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Entity
@Table(name = "tasks")
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class Task {

	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	private Long id;

	@Column(nullable = false, length = 200)
	private String title;

	@Column(length = 2000)
	private String description;

	@Enumerated(EnumType.STRING)
	@Column(nullable = false, length = 20)
	private TaskStatus status;

	@ManyToOne(fetch = FetchType.LAZY, optional = false)
	@JoinColumn(name = "team_id", nullable = false)
	private Team team;

	@ManyToOne(fetch = FetchType.LAZY)
	@JoinColumn(name = "assignee_id")
	private User assignee;

	@ManyToOne(fetch = FetchType.LAZY, optional = false)
	@JoinColumn(name = "created_by_id", nullable = false)
	private User createdBy;

	private LocalDate dueDate;

	@Column(nullable = false, updatable = false)
	private Instant createdAt;

	@Column(nullable = false)
	private Instant updatedAt;

	@Builder
	public Task(
			String title,
			String description,
			TaskStatus status,
			Team team,
			User assignee,
			User createdBy,
			LocalDate dueDate
	) {
		this.title = title;
		this.description = description;
		this.status = status == null ? TaskStatus.TODO : status;
		this.team = team;
		this.assignee = assignee;
		this.createdBy = createdBy;
		this.dueDate = dueDate;
	}

	@PrePersist
	void onCreate() {
		Instant now = Instant.now();
		this.createdAt = now;
		this.updatedAt = now;
		if (this.status == null) {
			this.status = TaskStatus.TODO;
		}
	}

	@PreUpdate
	void onUpdate() {
		this.updatedAt = Instant.now();
	}

	public void update(String title, String description, TaskStatus status, User assignee, LocalDate dueDate) {
		this.title = title;
		this.description = description;
		this.status = status;
		this.assignee = assignee;
		this.dueDate = dueDate;
	}
}
