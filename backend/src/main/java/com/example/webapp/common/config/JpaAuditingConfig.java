package com.example.webapp.common.config;

import org.springframework.context.annotation.Configuration;
import org.springframework.data.jpa.repository.config.EnableJpaAuditing;

/**
 * JPA Auditing 활성화 — {@code AuditableEntity}의 {@code @CreatedDate}/{@code @LastModifiedDate}를
 * 채운다.
 */
@Configuration
@EnableJpaAuditing
public class JpaAuditingConfig {

}
