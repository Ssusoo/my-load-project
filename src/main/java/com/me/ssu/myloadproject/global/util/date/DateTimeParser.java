package com.me.ssu.myloadproject.global.util.date;

import com.me.ssu.myloadproject.global.constant.DateTimeFormat;
import lombok.AccessLevel;
import lombok.NoArgsConstructor;
import org.apache.commons.lang3.StringUtils;

import java.time.DateTimeException;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
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
 * 문자열 -> 날짜 시간
 */
public class DateTimeParser {
	public static LocalDateTime parse(String date, String time) {
		// yyyyMMddHHmmss(20250703123045) -> parse(2000-01-01T00:00:00)
		var formatter = DateTimeFormatter.ofPattern(DateTimeFormat.BASIC_DATE_TIME_FORMAT);
		return parse(date, time, formatter);
	}

	public static LocalDateTime parse(String date, String time, DateTimeFormatter formatter) {
		return parse(date, time, formatter, null);
	}

	public static LocalDateTime parse(String date, String time, DateTimeFormatter formatter, LocalDateTime defaultValue) {
		if (StringUtils.isNotEmpty(date) && StringUtils.isNotEmpty(time)) {
			try {
				return LocalDateTime.parse(String.format("%s%s", date, time), formatter);
			} catch (DateTimeParseException ex) {
				return defaultValue;
			}
		}
		return defaultValue;
	}

	public static LocalDateTime getPeriodStartAt(Integer year, Integer month) {
		try {
			if (year != null && month != null) {
				return LocalDateTime.of(LocalDate.of(year, month, 1), LocalTime.MIN);
			}
			return null;
		} catch (DateTimeException e) {
			return null;
		}
	}

	public static LocalDateTime getPeriodEndAt(Integer year, Integer month) {
		var periodStartAt = getPeriodStartAt(year, month);
		if (periodStartAt != null) {
			return LocalDateTime.of(periodStartAt.plusMonths(1).minusDays(1).toLocalDate(), LocalTime.MAX);
		}
		return null;
	}
}
