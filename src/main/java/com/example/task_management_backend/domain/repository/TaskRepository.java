package com.example.task_management_backend.domain.repository;

import com.example.task_management_backend.domain.entity.Task;
import java.util.List;
import java.util.Optional;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

public interface TaskRepository extends JpaRepository<Task, Long> {

	@Query("""
			SELECT t FROM Task t
			LEFT JOIN FETCH t.assignee
			JOIN FETCH t.createdBy
			WHERE t.team.id = :teamId
			ORDER BY t.createdAt DESC
			""")
	List<Task> findByTeamIdWithDetails(@Param("teamId") Long teamId);

	@Query("""
			SELECT t FROM Task t
			LEFT JOIN FETCH t.assignee
			JOIN FETCH t.createdBy
			JOIN FETCH t.team
			WHERE t.id = :taskId AND t.team.id = :teamId
			""")
	Optional<Task> findByIdAndTeamIdWithDetails(@Param("taskId") Long taskId, @Param("teamId") Long teamId);
}
