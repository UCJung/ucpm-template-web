package com.example.webapp.common.domain;

import java.time.Instant;

import org.springframework.data.annotation.CreatedDate;
import org.springframework.data.annotation.LastModifiedDate;
import org.springframework.data.jpa.domain.support.AuditingEntityListener;

import jakarta.persistence.Column;
import jakarta.persistence.EntityListeners;
import jakarta.persistence.MappedSuperclass;

/**
 * 전 엔티티 공통 감사필드(생성일시·수정일시)를 제공하는 상위 클래스.
 * <p>이 템플릿은 HelloWorld 수준으로 타임스탬프 2종만 둔다. 생성자·수정자(User 참조 FK)가 필요하면
 * {@code @CreatedBy}/{@code @LastModifiedBy} + AuditorAware로 확장한다.</p>
 */
@MappedSuperclass
@EntityListeners(AuditingEntityListener.class)
public abstract class AuditableEntity {

	@CreatedDate
	@Column(name = "created_at", nullable = false, updatable = false)
	private Instant createdAt;

	@LastModifiedDate
	@Column(name = "updated_at", nullable = false)
	private Instant updatedAt;

	public Instant getCreatedAt() {
		return createdAt;
	}

	public Instant getUpdatedAt() {
		return updatedAt;
	}

}
