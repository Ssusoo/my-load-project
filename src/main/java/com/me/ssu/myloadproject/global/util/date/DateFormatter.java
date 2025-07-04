package com.me.ssu.myloadproject.global.util.date;

import com.me.ssu.myloadproject.global.constant.DateTimeFormat;
import lombok.AccessLevel;
import lombok.NoArgsConstructor;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.time.format.DateTimeParseException;

@SuppressWarnings("unused")
@NoArgsConstructor(access = AccessLevel.PRIVATE)
/**
 * 날짜 -> 문자열
 *  API 응답값 포맷팅, 로그, 파일명 등 문자열로 출력
 */
public class DateFormatter {
	public static String from(LocalDateTime dateTime) {
		return from(dateTime, DateTimeFormat.DATE_TIME_FORMAT);
	}

	public static String from(LocalDateTime dateTime, String pattern) {
		if (dateTime != null) {
			try {
				return dateTime.format(DateTimeFormatter.ofPattern(pattern));
			} catch (DateTimeParseException ex) {
				return null;
			}
		}
		return null;
	}

	public static String from(LocalDate date) {
		return from(date, DateTimeFormat.BASIC_DATE_FORMAT);
	}

	public static String from(LocalDate date, String pattern) {
		if (date != null) {
			try {
				return date.format(DateTimeFormatter.ofPattern(pattern));
			} catch (DateTimeParseException ex) {
				return null;
			}
		}
		return null;
	}
}
