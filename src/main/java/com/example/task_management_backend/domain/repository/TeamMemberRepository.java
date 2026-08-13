package com.example.task_management_backend.domain.repository;

import com.example.task_management_backend.domain.entity.TeamMember;
import java.util.List;
import java.util.Optional;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

public interface TeamMemberRepository extends JpaRepository<TeamMember, Long> {

	@Query("""
			SELECT m FROM TeamMember m
			JOIN FETCH m.team t
			JOIN FETCH t.owner
			WHERE m.user.id = :userId
			ORDER BY t.createdAt DESC
			""")
	List<TeamMember> findByUserIdWithTeam(@Param("userId") Long userId);

	@Query("""
			SELECT m FROM TeamMember m
			JOIN FETCH m.user
			WHERE m.team.id = :teamId
			ORDER BY m.joinedAt ASC
			""")
	List<TeamMember> findByTeamIdWithUser(@Param("teamId") Long teamId);

	Optional<TeamMember> findByTeamIdAndUserId(Long teamId, Long userId);

	boolean existsByTeamIdAndUserId(Long teamId, Long userId);
}
