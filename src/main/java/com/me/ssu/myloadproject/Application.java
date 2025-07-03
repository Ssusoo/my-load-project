package com.me.ssu.myloadproject;

import jakarta.servlet.ServletContext;
import jakarta.servlet.ServletRegistration;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.builder.SpringApplicationBuilder;
import org.springframework.boot.web.servlet.support.SpringBootServletInitializer;
import org.springframework.web.WebApplicationInitializer;
import org.springframework.web.context.support.AnnotationConfigWebApplicationContext;
import org.springframework.web.servlet.DispatcherServlet;

/**
 * @SpringBootApplication만으로 Spring Boot가 자동으로
 *  DispatcherServlet을 만들어서 동작함.
 *
 * SpringBootServletInitializer : war 파일로 배포할 때 스프링 부트 앱이 잘 실행할 수 있게 도와 줌.
 *
 * WebApplicationInitializer : web.xml 없어도 서블릿 등록하고 설정해주는 인터페이스
 *  onStartup() 메서드로 디스페처 서블릿을 수동 등록하거나 추가 설정 가능.
 */
@SpringBootApplication
public class Application extends SpringBootServletInitializer implements WebApplicationInitializer {
	public static void main(String[] args) {
		SpringApplication.run(Application.class, args);
	}

	/**
	 * 외부 WAS가 스프링 부트 애플리케이션을 실행할 수 있게
	 * 스프링 애플리케이션 컨텍스트를 초기화해주는 역할을 함.
	 */
	@Override
	protected SpringApplicationBuilder configure(SpringApplicationBuilder application) {
		return application.sources(Application.class);
	}

	/**
	 * HttpServletRequest 를 Thread safe 하게 사용할 수 있도록 설정 추가
	 *  onStartup()
	 *   1) SpringBoot는 기본적으로 디스페처 서블릿을 자동을 등록하지만
	 *      setThreadContextInheritable(true) 같은 커스텀 설정을 못하기에
	 *      직접 onStartup()으로 직접 등록
	 *   2) 비동기 처리를 하거나 새 스레드에서 로그 정보가 필요할 때
	 */
	@Override
	public void onStartup(ServletContext servletContext) {
		AnnotationConfigWebApplicationContext applicationContext = new AnnotationConfigWebApplicationContext();
		DispatcherServlet dispatcherServlet = new DispatcherServlet(applicationContext);

		// setThreadContextInheritable(true) : 자식 스레드에서도 상속하도록 만들어 줌.
		//  자바는 ThreadLocal이라는 기능을 이용해 요청(Request) 단위의 정보를 저장한다.
		//  새로운 스레드를 만들어서 작업을 한 경우 부모 스레드의 ThreadLocal 데이터를 모름
		dispatcherServlet.setThreadContextInheritable(true);
		ServletRegistration.Dynamic dispatcher = servletContext.addServlet("dispatcherServlet", dispatcherServlet);
		dispatcher.setLoadOnStartup(1);
		dispatcher.addMapping("/");
	}
}
