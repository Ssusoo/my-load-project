package com.me.ssu.myloadproject.base;

import com.me.ssu.myloadproject.global.dto.Pagination;
import com.querydsl.core.types.EntityPath;
import com.querydsl.core.types.Expression;
import com.querydsl.core.types.Projections;
import com.querydsl.core.types.dsl.Expressions;
import com.querydsl.jpa.impl.JPAInsertClause;
import com.querydsl.jpa.impl.JPAQuery;
import com.querydsl.jpa.impl.JPAQueryFactory;
import com.querydsl.jpa.impl.JPAUpdateClause;
import jakarta.persistence.EntityManager;
import jakarta.persistence.PersistenceContext;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.support.JpaEntityInformation;
import org.springframework.data.jpa.repository.support.JpaEntityInformationSupport;
import org.springframework.data.support.PageableExecutionUtils;
import org.springframework.data.util.ProxyUtils;
import org.springframework.transaction.annotation.Transactional;

import java.util.ArrayList;
import java.util.List;

@SuppressWarnings("all")
@Transactional(readOnly = true)
public abstract class BaseRepository<E, ID> {
	@PersistenceContext
	private EntityManager em;

	@Autowired
	private JPAQueryFactory query;
	private JpaEntityInformation<E, ID> entityInformation;

	/**
	 * JPAQuery
	 *  QueryDSL 쿼리 짜기 위해 공용 템플릿
	 */
	protected <DTO> JPAQuery<DTO> select(Expression<DTO> expressions) {
		return query.select(expressions);
	}

	protected <DTO> JPAQuery<DTO> select(Class<DTO> clazz, Expression<?>... expressions) {
		return query.select(Projections.constructor(clazz, expressions));
	}

	protected JPAQuery<Integer> selectOne() {
		return query.selectOne();
	}

	protected JPAQuery<E> selectFrom(EntityPath<E> from) {
		return query.selectFrom(from);
	}

	protected <C extends Expression<?>> JPAQuery<String> selectGroupConcat(C column) {
		return select(Expressions.stringTemplate("group_concat({0})", column));
	}

	protected JPAUpdateClause update(EntityPath<E> path) {
		return query.update(path);
	}

	protected JPAInsertClause insert(EntityPath<E> path) {
		return query.insert(path);
	}

	/**
	 * 엔티티 저장/병합 관련 메서드
	 */
	@Transactional
	public E save(E entity) {
		if (Boolean.TRUE.equals(isNewEntity(entity))) {
			em.persist(entity);
			return entity;
		}

		return em.merge(entity);
	}

	@Transactional
	public List<E> saveAll(Iterable<E> entities) {
		List<E> result = new ArrayList<E>();
		for (E entity : entities) {
			result.add(save(entity));
		}

		return result;
	}

	@Transactional
	public E saveAndFlush(E entity) {

		E result = save(entity);
		flush();

		return result;
	}

	@Transactional
	public List<E> saveAllAndFlush(Iterable<E> entities) {

		List<E> result = saveAll(entities);
		flush();

		return result;
	}

	/**
	 * 변경 사항을 DB에 강제로 반영
	 */
	@Transactional
	public void flush() {
		em.flush();
	}

	/**
	 * 영속성 컨텍스트 초기화
	 */
	@Transactional
	public void clear() {
		em.clear();
	}

	@SuppressWarnings("unchecked")
	@Transactional
	public void delete(E entity) {
		JpaEntityInformation<E, ID> entityInformation = this.getJpaEntityInformation(entity.getClass());
		if (isNewEntity(entity)) {
			return;
		}

		E existing = (E) em.find(ProxyUtils.getUserClass(entity), entityInformation.getId(entity));
		if (existing == null) {
			return;
		}

		em.remove(em.contains(entity) ? entity : em.merge(entity));
	}

	@SuppressWarnings("unchecked")
	private JpaEntityInformation<E, ID> getJpaEntityInformation(Class<?> clazz) {
		if (this.entityInformation == null) {
			this.entityInformation =
					(JpaEntityInformation<E, ID>) JpaEntityInformationSupport.getEntityInformation(clazz, em);
		}

		return this.entityInformation;
	}

	private Boolean isNewEntity(E entity) {
		return this.getJpaEntityInformation(entity.getClass()).isNew(entity);
	}

	/**
	 * 페이징 처리
	 */
	protected <DTO> Pagination<DTO> applyPagination(JPAQuery<DTO> contentQuery, JPAQuery<Long> countQuery, Pageable pageable) {
		return new Pagination<>(
				PageableExecutionUtils.getPage(contentQuery.offset(pageable.getOffset())
						.limit(pageable.getPageSize())
						.fetch(), pageable, countQuery::fetchOne));
	}
}
