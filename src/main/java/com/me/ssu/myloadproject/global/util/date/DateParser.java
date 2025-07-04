package com.me.ssu.myloadproject.global.util.date;

import com.me.ssu.myloadproject.global.constant.DateTimeFormat;
import lombok.AccessLevel;
import lombok.NoArgsConstructor;
import org.apache.commons.lang3.StringUtils;

import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.time.format.DateTimeParseException;

/**
 * @NoArgsConstructor(access = AccessLevel.PRIVATE)
 *  생성자 private 처리: 객체 생성을 막고, 정적 유틸 클래스로 사용하게 만듦
 *
 * @SuppressWarnings("unused")
 *  사용하지 않아도 경고 없애줌 (예: IDE에서 경고 뜨는 것 방지)
 */

/**
 * org.apache.commons.lang3.StringUtils
 *  문자열 안전하게 파싱 : 외부 라이브러리 사용
 */
@SuppressWarnings("unused")
@NoArgsConstructor(access = AccessLevel.PRIVATE)
/**
 * 문자열 -> 날짜로 변환
 */
public class DateParser {
	public static LocalDate parse(String date) {
		// yyyyMMdd(20250703) -> parse(2025-07-03
		var dateTimeFormatter = DateTimeFormatter.ofPattern(DateTimeFormat.BASIC_DATE_FORMAT);
		return parse(date, dateTimeFormatter);
	}

	public static LocalDate parse(String date, DateTimeFormatter dateTimeFormatter) {
		return parse(date, dateTimeFormatter, null);
	}

	public static LocalDate parse(String date, DateTimeFormatter dateTimeFormatter, LocalDate defaultValue) {
		// 빈 문자열 체크
		if (StringUtils.isNotEmpty(date)) {
			try {
				return LocalDate.parse(date, dateTimeFormatter);
			} catch (DateTimeParseException ex) {
				return defaultValue;
			}
		}
		return defaultValue;
	}
}
