package com.me.ssu.myloadproject.global.config.jpa;

import com.querydsl.jpa.impl.JPAQueryFactory;
import jakarta.persistence.EntityManager;
import jakarta.persistence.PersistenceContext;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

/**
 * @PersistenceContext
 *  내부적으로 DB와의 실제 연결, 쿼리 실행 등을 담당하는 역할
 */
@Configuration
public class QuerydslConfig {
	@PersistenceContext
	private EntityManager entityManager;

	/**
	 * Q객체 기반으로 쿼리 빌드 가능
	 */
	@Bean
	public JPAQueryFactory jpaQueryFactory() {
		return new JPAQueryFactory(entityManager);
	}
}
