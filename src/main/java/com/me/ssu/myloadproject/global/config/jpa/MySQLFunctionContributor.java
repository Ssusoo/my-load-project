package com.me.ssu.myloadproject.global.config.jpa;

import org.hibernate.boot.model.FunctionContributions;
import org.hibernate.boot.model.FunctionContributor;
import org.hibernate.dialect.function.StandardSQLFunction;
import org.hibernate.type.StandardBasicTypes;

/**
 * Hibernate 사용자 정의 SQL 함수
 *  MySQL의 그룹 함수를 Hibernate에서도 쿼리로 사용할 수 있게 등록
 */
public class MySQLFunctionContributor implements FunctionContributor {
	@Override
	public void contributeFunctions(FunctionContributions functionContributions) {
		functionContributions.getFunctionRegistry()
				.register("group_concat", new StandardSQLFunction("group_concat", StandardBasicTypes.STRING));
	}
}
