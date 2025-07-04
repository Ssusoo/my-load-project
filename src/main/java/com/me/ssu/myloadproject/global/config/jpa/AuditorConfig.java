package com.me.ssu.myloadproject.global.config.jpa;

import lombok.RequiredArgsConstructor;
import org.springframework.context.annotation.Configuration;
import org.springframework.data.jpa.repository.config.EnableJpaAuditing;

/**
 * @EnableJpaAuditing
 *  JPA의 감사 기능을 활성화
 *      엔티티를 저장하거나 수정할 때
 *      자동으로 날짜나 사용자를 기록해주는 기능
 *
 * @CreatedDate : 저장시 자동으로 값 채움 or @PrePersist
 *
 * @LastModifiedDate : 수정시 자동으로 값 갱신 or @PreUpdate
 */
@Configuration
@EnableJpaAuditing
@RequiredArgsConstructor
public class AuditorConfig {
}
