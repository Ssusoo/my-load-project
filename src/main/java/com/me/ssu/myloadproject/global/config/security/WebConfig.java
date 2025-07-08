package com.me.ssu.myloadproject.global.config.security;

import com.me.ssu.myloadproject.global.application.StringToEnumConverterFactory;
import lombok.RequiredArgsConstructor;
import org.springframework.context.annotation.Configuration;
import org.springframework.format.FormatterRegistry;
import org.springframework.format.datetime.standard.DateTimeFormatterRegistrar;
import org.springframework.web.servlet.config.annotation.CorsRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

/**
 * @Configuration : Spring 설정 클래스임(빈으로 등록)
 *
 * WebMvcConfigurer
 *  SpringMVC의 동작을 커스터마이징할 수 있는 인터페이스
 */
@Configuration
@RequiredArgsConstructor
public class WebConfig implements WebMvcConfigurer {

	// CORS 설정 : 도메인이 다른 프론트엔드가 백엔드에 요청할 수 있게 허용
	@Override
	public void addCorsMappings(CorsRegistry registry) {
		registry
				.addMapping("/**")        // 모든 경로에 대해
				.maxAge(3600);                      // 3600초(1시간) 동안 캐시
	}

	// 날짜 및 Enum 변환 설정
	@Override
	public void addFormatters(FormatterRegistry registry) {
		DateTimeFormatterRegistrar dateTimeFormatterRegistrar = new DateTimeFormatterRegistrar();

		// ISO 8601 형식 사용
		// 예: "2024-01-01" → LocalDate, "2024-01-01T10:00:00" → LocalDateTime으로 자동 변환 가능
		dateTimeFormatterRegistrar.setUseIsoFormat(true);

		dateTimeFormatterRegistrar.registerFormatters(registry);

		// 프론트에서 보낸 문자열 코드(예: "A", "I")를 내부 Enum으로 자동 매핑해줌
		registry.addConverterFactory(new StringToEnumConverterFactory());
	}
}
