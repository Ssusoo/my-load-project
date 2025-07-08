package com.me.ssu.myloadproject.global.config;

import com.fasterxml.jackson.core.JsonParser;
import com.fasterxml.jackson.databind.DeserializationContext;
import com.fasterxml.jackson.databind.JsonDeserializer;
import com.fasterxml.jackson.databind.module.SimpleModule;
import com.fasterxml.jackson.datatype.jsr310.ser.LocalDateSerializer;
import com.fasterxml.jackson.datatype.jsr310.ser.LocalDateTimeSerializer;
import com.me.ssu.myloadproject.global.constant.DateTimeFormat;
import org.springframework.boot.autoconfigure.jackson.Jackson2ObjectMapperBuilderCustomizer;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import java.io.IOException;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.time.format.DateTimeFormatter;
import java.util.Locale;
import java.util.TimeZone;

/**
 * 전역 포맷 설정 : 스프링 부트에서 Jackson을 통해 날짜와 시간을 Json으로 변환하거나 다시 읽기
 */
@Configuration
public class JacksonConfig {

	/**
	 * 응답(JSON 직렬화) 시 날짜/시간 포맷 지정
	 *  직렬화 : Object -> Json 문자열로 변환
	 */
	@Bean
	public Jackson2ObjectMapperBuilderCustomizer jackson2ObjectMapperBuilderCustomizer() {
		return builder -> {
			// ObjectMapper에 대한 직렬화(응답시 Json 변환)설정
			builder.simpleDateFormat(DateTimeFormat.DATE_TIME_FORMAT);
			builder.timeZone(TimeZone.getTimeZone("Asia/Seoul"));
			builder.locale(Locale.getDefault());

			// LocalDate을 원하는 포맷으로 출력(yyyy-MM-dd)
			builder.serializers(new LocalDateSerializer(DateTimeFormatter.ofPattern(DateTimeFormat.DATE_FORMAT)));

			// LocalDateTime을 원하는 포멧으로 출력(yyyyMMdd)
			builder.serializers(new LocalDateTimeSerializer(DateTimeFormatter.ofPattern(DateTimeFormat.BASIC_DATE_FORMAT)));
		};
	}

	/**
	 * 요청(JSON 역직렬화) 시 날짜/시간 포맷 지정
	 *  역직렬화 : Json -> Object로 변환
	 */
	@Bean
	public SimpleModule jsonMapperJava8DateTimeModule() {
		SimpleModule module = new SimpleModule();
		module.addDeserializer(LocalDate.class, new JsonDeserializer<>() {
			@Override
			public LocalDate deserialize(JsonParser jsonParser, DeserializationContext deserializationContext) throws IOException {
				return LocalDate.parse(jsonParser.getValueAsString(), DateTimeFormatter.ofPattern(DateTimeFormat.DATE_FORMAT));
			}
		});
		module.addDeserializer(LocalTime.class, new JsonDeserializer<>() {
			@Override
			public LocalTime deserialize(JsonParser jsonParser, DeserializationContext deserializationContext) throws IOException {
				return LocalTime.parse(jsonParser.getValueAsString(), DateTimeFormatter.ofPattern(DateTimeFormat.TIME_FORMAT));
			}
		});
		module.addDeserializer(LocalDateTime.class, new JsonDeserializer<>() {
			@Override
			public LocalDateTime deserialize(JsonParser jsonParser, DeserializationContext deserializationContext) throws IOException {
				return LocalDateTime.parse(jsonParser.getValueAsString(), DateTimeFormatter.ofPattern(DateTimeFormat.DATE_TIME_FORMAT));
			}
		});
		return module;
	}
}
