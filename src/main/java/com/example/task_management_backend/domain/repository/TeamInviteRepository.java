package com.example.task_management_backend.domain.repository;

import com.example.task_management_backend.domain.entity.TeamInvite;
import java.util.Optional;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

public interface TeamInviteRepository extends JpaRepository<TeamInvite, Long> {

	@Query("""
			SELECT i FROM TeamInvite i
			JOIN FETCH i.team t
			JOIN FETCH t.owner
			WHERE i.token = :token
			""")
	Optional<TeamInvite> findByTokenWithTeam(@Param("token") String token);
}
