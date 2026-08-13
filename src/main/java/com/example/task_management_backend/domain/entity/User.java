package com.example.task_management_backend.domain.entity;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.PrePersist;
import jakarta.persistence.Table;
import java.time.Instant;
import lombok.AccessLevel;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Entity
@Table(name = "users")
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class User {

	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	private Long id;

	@Column(nullable = false, unique = true, length = 100)
	private String email;

	@Column(nullable = false)
	private String password;

	@Column(nullable = false, length = 50)
	private String name;

	@Column(nullable = false, updatable = false)
	private Instant createdAt;

	@Builder
	public User(String email, String password, String name) {
		this.email = email;
		this.password = password;
		this.name = name;
	}

	@PrePersist
	void onCreate() {
		this.createdAt = Instant.now();
	}

	public void updateProfile(String name) {
		this.name = name;
	}
}
