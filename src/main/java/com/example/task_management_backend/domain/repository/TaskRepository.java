package com.example.task_management_backend.domain.repository;

import com.example.task_management_backend.domain.entity.Task;
import java.util.List;
import org.springframework.data.jpa.repository.JpaRepository;

public interface TaskRepository extends JpaRepository<Task, Long> {

	List<Task> findByTeamId(Long teamId);
}
