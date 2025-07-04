package com.me.ssu.myloadproject.global.config.database;

import com.zaxxer.hikari.HikariDataSource;
import jakarta.persistence.EntityManager;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.boot.autoconfigure.EnableAutoConfiguration;
import org.springframework.boot.autoconfigure.jdbc.DataSourceAutoConfiguration;
import org.springframework.boot.autoconfigure.orm.jpa.JpaProperties;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.boot.jdbc.DataSourceBuilder;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Primary;
import org.springframework.core.env.Environment;
import org.springframework.data.jpa.repository.config.EnableJpaRepositories;
import org.springframework.orm.jpa.JpaTransactionManager;
import org.springframework.orm.jpa.LocalContainerEntityManagerFactoryBean;
import org.springframework.orm.jpa.vendor.HibernateJpaVendorAdapter;
import org.springframework.transaction.annotation.EnableTransactionManagement;

import javax.sql.DataSource;
import java.util.HashMap;
import java.util.Properties;

@Slf4j
/**
 * @Configuration :
 *  해당 클래스가 스프링 설정 클래스인지 명시
 */
@Configuration
@RequiredArgsConstructor
/**
 * @EnableTransactionManagement :
 *  1) @Transactional할 때 필요.
 *  2) 트랜잭션 관리가 적용.
 *  3) 내부적으로 TransactionManager를 스프링이 찾아서 연결해 줌.
 */
@EnableTransactionManagement
/**
 * @EnableAutoConfiguration :
 *  Spring Boot가 자동으로 DB 설정을 하지 않도록 막는 설정
 */
@EnableAutoConfiguration(exclude = {DataSourceAutoConfiguration.class})
@EnableJpaRepositories(basePackages = "*.*.*.*.domains.*.repository")
public class DataSourceConfig {
	private final Environment env;
	private final JpaProperties jpaProperties;

	/**
	 * @Bean : 메서드에 선언시 스프링 컨테이너가 해당 메서드를 실행해
	 *       : 객체(Bean)으로 등록
	 */
	@Bean
	@ConfigurationProperties(prefix = "spring.datasource.master")
	public DataSource masterDataSource() {
		return DataSourceBuilder.create().type(HikariDataSource.class).build();
	}

	/**
	 * @Bean : 메서드에 선언시 스프링 컨테이너가 해당 메서드를 실행 해
	 *       : 객체(Bean)으로 등록
	 */
	@Bean
	@ConfigurationProperties(prefix = "spring.datasource.slave")
	public DataSource slaveDataSource() {
		return DataSourceBuilder.create().type(HikariDataSource.class).build();
	}

	/**
	 * @Transactional(readOnly = true)인지 여부에 따라
	 *  master 또는 slave DB로 자동 연결되도록 설정하는 커스텀
	 *  DataSource 빈을 등록하는 메서드
	 */
	@Bean
	public DataSource routingDataSource(@Qualifier("masterDataSource") DataSource masterDataSource,
	                                    @Qualifier("slaveDataSource") DataSource slaveDataSource) {
		var routingDataSource = new ReplicationRoutingDataSource();
		var dataSourceMap = new HashMap<>();
		dataSourceMap.put("master", masterDataSource);
		dataSourceMap.put("slave", slaveDataSource);
		routingDataSource.setTargetDataSources(dataSourceMap);
		routingDataSource.setDefaultTargetDataSource(masterDataSource);

		return routingDataSource;
	}

	/**
	 * LocalContainerEntityManagerFactoryBean
	 * EntityManager 를 생성하는 팩토리
	 * SessionFactoryBean 과 동일한 역할, Datasource 와 mapper 를 스캔할 .xml 경로를 지정하듯이
	 * datasource 와 엔티티가 저장된 폴더 경로를 매핑해주면 된다.
	 */
	@Primary
	@Bean
	public LocalContainerEntityManagerFactoryBean entityManagerFactory(
			@Qualifier("dataSource") DataSource dataSource) {
		LocalContainerEntityManagerFactoryBean em = new LocalContainerEntityManagerFactoryBean();
		em.setDataSource(dataSource);
		em.setPackagesToScan("*.*.*.*.domains.*.domain");

		HibernateJpaVendorAdapter vendorAdapter = new HibernateJpaVendorAdapter();
		em.setJpaVendorAdapter(vendorAdapter);
		em.setJpaProperties(additionalProperties());
		return em;
	}

	/**
	 * JpaTransactionManager : EntityManagerFactory 를 전달받아 JPA 에서 트랜잭션을 관리
	 */
	@Primary
	@Bean
	public JpaTransactionManager transactionManager(
			@Qualifier("entityManagerFactory") LocalContainerEntityManagerFactoryBean mfBean) {
		JpaTransactionManager transactionManager = new JpaTransactionManager();
		transactionManager.setEntityManagerFactory(mfBean.getObject());
		return transactionManager;
	}

	/**
	 * application.yml 내용을 자바 코드로 불러와
	 * Hibernate 설정 넘기는 메서드
	 */
	private Properties additionalProperties() {
		Properties properties = new Properties();
		properties.putAll(jpaProperties.getProperties());
		properties.setProperty("hibernate.hbm2ddl.auto", env.getProperty("spring.jpa.hibernate.ddl-auto"));
		properties.setProperty("hibernate.show_sql", env.getProperty("spring.jpa.show-sql"));
		return properties;
	}
}
