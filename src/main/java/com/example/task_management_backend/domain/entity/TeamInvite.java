package com.example.task_management_backend.domain.entity;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.FetchType;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.PrePersist;
import jakarta.persistence.Table;
import java.time.Instant;
import java.util.UUID;
import lombok.AccessLevel;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Entity
@Table(name = "team_invites")
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class TeamInvite {

	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	private Long id;

	@ManyToOne(fetch = FetchType.LAZY, optional = false)
	@JoinColumn(name = "team_id", nullable = false)
	private Team team;

	@ManyToOne(fetch = FetchType.LAZY, optional = false)
	@JoinColumn(name = "created_by_id", nullable = false)
	private User createdBy;

	@Column(nullable = false, unique = true, length = 64)
	private String token;

	@Column(nullable = false)
	private Instant expiresAt;

	@Column(nullable = false)
	private boolean revoked;

	@Column(nullable = false, updatable = false)
	private Instant createdAt;

	@Builder
	public TeamInvite(Team team, User createdBy, Instant expiresAt) {
		this.team = team;
		this.createdBy = createdBy;
		this.expiresAt = expiresAt;
		this.token = UUID.randomUUID().toString().replace("-", "");
		this.revoked = false;
	}

	@PrePersist
	void onCreate() {
		this.createdAt = Instant.now();
	}

	public boolean isUsable() {
		return !revoked && Instant.now().isBefore(expiresAt);
	}
}
