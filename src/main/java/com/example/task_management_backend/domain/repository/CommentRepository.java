package com.example.task_management_backend.domain.repository;

import com.example.task_management_backend.domain.entity.Comment;
import java.util.List;
import org.springframework.data.jpa.repository.JpaRepository;

public interface CommentRepository extends JpaRepository<Comment, Long> {

	List<Comment> findByTaskIdOrderByCreatedAtAsc(Long taskId);
}
