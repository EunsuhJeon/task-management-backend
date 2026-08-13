package com.example.task_management_backend.domain.repository;

import com.example.task_management_backend.domain.entity.Comment;
import java.util.List;
import java.util.Optional;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

public interface CommentRepository extends JpaRepository<Comment, Long> {

	@Query("""
			SELECT c FROM Comment c
			JOIN FETCH c.author
			JOIN FETCH c.task t
			WHERE t.id = :taskId AND t.team.id = :teamId
			ORDER BY c.createdAt ASC
			""")
	List<Comment> findByTeamIdAndTaskIdWithAuthor(
			@Param("teamId") Long teamId,
			@Param("taskId") Long taskId
	);

	@Query("""
			SELECT c FROM Comment c
			JOIN FETCH c.author
			JOIN FETCH c.task t
			JOIN FETCH t.team
			WHERE c.id = :commentId AND t.id = :taskId AND t.team.id = :teamId
			""")
	Optional<Comment> findByIdAndTaskIdAndTeamId(
			@Param("commentId") Long commentId,
			@Param("taskId") Long taskId,
			@Param("teamId") Long teamId
	);
}
