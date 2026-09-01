-- 초기 스키마 (postgres 프로파일). local(H2)은 JPA ddl-auto가 스키마를 생성하므로 이 파일을 쓰지 않는다.
-- 엔티티(User + AuditableEntity)와 컬럼이 일치해야 한다(JPA validate).
CREATE TABLE users (
    id           BIGSERIAL    PRIMARY KEY,
    username     VARCHAR(255) NOT NULL,
    password     VARCHAR(255) NOT NULL,
    display_name VARCHAR(100),
    role         VARCHAR(20)  NOT NULL,
    status       VARCHAR(20)  NOT NULL,
    created_at   TIMESTAMPTZ  NOT NULL,
    updated_at   TIMESTAMPTZ  NOT NULL,
    CONSTRAINT uk_users_username UNIQUE (username)
);
