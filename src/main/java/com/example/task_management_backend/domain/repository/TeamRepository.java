package com.example.task_management_backend.domain.repository;

import com.example.task_management_backend.domain.entity.Team;
import org.springframework.data.jpa.repository.JpaRepository;

public interface TeamRepository extends JpaRepository<Team, Long> {
}
