package com.example.webapp.common.config;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.boot.CommandLineRunner;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import com.example.webapp.user.domain.User;
import com.example.webapp.user.domain.UserRole;
import com.example.webapp.user.repository.UserRepository;

/**
 * 부트스트랩 편의 — 사용자가 하나도 없으면 데모 관리자 계정을 시드한다(idempotent).
 * 로그인/보호 API를 바로 시험할 수 있게 한다. 운영에서는 제거하거나 프로파일로 제한한다.
 * <p>기본 계정: {@code admin} / {@code admin1234}</p>
 */
@Configuration
public class DataInitializer {

	private static final Logger log = LoggerFactory.getLogger(DataInitializer.class);

	@Bean
	CommandLineRunner seedDemoUser(UserRepository userRepository,
			org.springframework.security.crypto.password.PasswordEncoder passwordEncoder) {
		return args -> {
			if (userRepository.count() > 0) {
				return;
			}
			User admin = new User("admin", passwordEncoder.encode("admin1234"), "관리자");
			admin.setRole(UserRole.ADMIN);
			userRepository.save(admin);
			log.info("데모 계정 시드 완료: admin / admin1234");
		};
	}

}
