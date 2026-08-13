package com.example.task_management_backend.config;

import com.example.task_management_backend.domain.entity.Task;
import com.example.task_management_backend.domain.entity.Team;
import com.example.task_management_backend.domain.entity.TeamMember;
import com.example.task_management_backend.domain.entity.User;
import com.example.task_management_backend.domain.enums.TaskStatus;
import com.example.task_management_backend.domain.enums.TeamRole;
import com.example.task_management_backend.domain.repository.TaskRepository;
import com.example.task_management_backend.domain.repository.TeamMemberRepository;
import com.example.task_management_backend.domain.repository.TeamRepository;
import com.example.task_management_backend.domain.repository.UserRepository;
import java.time.LocalDate;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.ApplicationArguments;
import org.springframework.boot.ApplicationRunner;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

@Slf4j
@Component
@RequiredArgsConstructor
@ConditionalOnProperty(name = "app.seed.enabled", havingValue = "true")
public class DemoDataLoader implements ApplicationRunner {

	private final UserRepository userRepository;
	private final TeamRepository teamRepository;
	private final TeamMemberRepository teamMemberRepository;
	private final TaskRepository taskRepository;
	private final PasswordEncoder passwordEncoder;

	@Override
	@Transactional
	public void run(ApplicationArguments args) {
		if (userRepository.existsByEmail("admin@demo.com")) {
			log.info("Demo seed skipped — admin@demo.com already exists");
			return;
		}

		User admin = userRepository.save(User.builder()
				.email("admin@demo.com")
				.password(passwordEncoder.encode("password123"))
				.name("Demo Admin")
				.build());

		User member = userRepository.save(User.builder()
				.email("member@demo.com")
				.password(passwordEncoder.encode("password123"))
				.name("Demo Member")
				.build());

		Team team = teamRepository.save(Team.builder()
				.name("Demo Team")
				.description("Seeded workspace for portfolio demos")
				.owner(admin)
				.build());

		teamMemberRepository.save(TeamMember.builder().team(team).user(admin).role(TeamRole.ADMIN).build());
		teamMemberRepository.save(TeamMember.builder().team(team).user(member).role(TeamRole.MEMBER).build());

		taskRepository.save(Task.builder()
				.title("Plan sprint board")
				.description("Outline columns and workflows")
				.status(TaskStatus.TODO)
				.team(team)
				.assignee(admin)
				.createdBy(admin)
				.dueDate(LocalDate.now().plusDays(2))
				.build());

		taskRepository.save(Task.builder()
				.title("Implement invite flow")
				.description("Shareable invite links")
				.status(TaskStatus.DOING)
				.team(team)
				.assignee(member)
				.createdBy(admin)
				.dueDate(LocalDate.now())
				.build());

		taskRepository.save(Task.builder()
				.title("Polish profile page")
				.description("Name update UX")
				.status(TaskStatus.DONE)
				.team(team)
				.assignee(member)
				.createdBy(member)
				.dueDate(LocalDate.now().minusDays(1))
				.build());

		log.info("Demo seed ready — admin@demo.com / member@demo.com (password123)");
	}
}
