package com.example.task_management_backend.domain.repository;

import com.example.task_management_backend.domain.entity.TeamMember;
import java.util.List;
import java.util.Optional;
import org.springframework.data.jpa.repository.JpaRepository;

public interface TeamMemberRepository extends JpaRepository<TeamMember, Long> {

	List<TeamMember> findByUserId(Long userId);

	Optional<TeamMember> findByTeamIdAndUserId(Long teamId, Long userId);

	boolean existsByTeamIdAndUserId(Long teamId, Long userId);
}
