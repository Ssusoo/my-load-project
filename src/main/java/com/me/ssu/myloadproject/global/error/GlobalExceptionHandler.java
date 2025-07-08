package com.me.ssu.myloadproject.global.error;

import com.me.ssu.myloadproject.global.constant.ActiveProfile;
import com.me.ssu.myloadproject.global.constant.ApiResponseCode;
import com.me.ssu.myloadproject.global.dto.ErrorResponse;
import com.me.ssu.myloadproject.global.error.exception.BusinessRuntimeException;
import jakarta.validation.ConstraintViolationException;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.core.env.Environment;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.http.converter.HttpMessageConversionException;
import org.springframework.http.converter.HttpMessageNotReadableException;
import org.springframework.validation.BindException;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import org.springframework.web.method.annotation.MethodArgumentTypeMismatchException;

import java.util.Map;

@Slf4j
@RestControllerAdvice
@RequiredArgsConstructor
public class GlobalExceptionHandler {
	private final Environment environment;

	/**
	 * 유효성 검사 : 클라이언트가 잘못된 요청을 보냈을 때
	 *  에러 메시지 Json 응답으로 반환 처리
	 */
	@ExceptionHandler({
			BindException.class, // 바인딩 실패시 발생
			MethodArgumentNotValidException.class // @RequestBody + @Valid 조합해서 유효성 검사 실패 시 발생
	})
	protected ResponseEntity<ErrorResponse> handleBindException(BindException ex) {
		return setResponse(ErrorResponse.of(ApiResponseCode.INVALID_INPUT_VALUE, ex.getBindingResult()));
	}

	/**
	 * 유효성 검사 : @RequestParam Long id(숫자)일 경우
	 *  GET /book?id=abc(문자)
	 *      타입 에러
	 */
	@ExceptionHandler(MethodArgumentTypeMismatchException.class)
	protected ResponseEntity<ErrorResponse> handleMethodArgumentTypeMismatchException(MethodArgumentTypeMismatchException ex) {
		return setResponse(ErrorResponse.of(ex));
	}

	/**
	 * 유효성 검사 : @RequestParam @Size(min = 5) String name
	 *  길이 관련 예외 처리
	 */
	@ExceptionHandler(ConstraintViolationException.class)
	protected ResponseEntity<ErrorResponse> handleConstraintViolationException(
			ConstraintViolationException ex) {
		return setResponse(ErrorResponse.of(ex));
	}

	/**
	 * 유효성 검사 : @RequestBody로 받는 값이 잘못된 경우
	 *  1) Json 형식 오류, 2) 타입 불일치, 3) 필드가 존재하지 않거나 잘못된 구조,
	 *  4) Content-Type: application/json인데 body가 아예 없음
	 */
	@ExceptionHandler(HttpMessageNotReadableException.class)
	protected ResponseEntity<ErrorResponse> handleHttpMessageNotReadableException(HttpMessageNotReadableException ex) {
		return setResponse(ErrorResponse.of(ApiResponseCode.BODY_READ_FAILED, ex.getMessage()));
	}

	/**
	 * 유효성 검사 : JSON Converter 에러
	 *  1) 주로, dto 클래스에 Getter 메서드가 누락된 경우 발생한다.
	 *  2) Jackson 등 메시지 컨버터가 Json -> Java 객체로 바꾸는 중 실패
	 */
	@ExceptionHandler(HttpMessageConversionException.class)
	protected ResponseEntity<ErrorResponse> handleHttpMessageConversionException(HttpMessageConversionException ex) {
		log.error("handleHttpMessageConversionException"); // 로그만 남기기
		throw ex;
	}

	/**
	 * 유효성 검사 : UnChecked Exception(비검사 예외) : 복구가 불가능한 예외 처리
	 */
	@ExceptionHandler({ BusinessRuntimeException.class})
	protected ResponseEntity<ErrorResponse> handleBusinessException(final BusinessRuntimeException ex) {
		return setResponse(ErrorResponse.of(ex.getErrorCode(), ex.getErrorMessage(), ex.getViolations()));
	}

	private ResponseEntity<ErrorResponse> setResponse(ErrorResponse errorResponse) {
		for (String profileName : environment.getActiveProfiles()) {
			if (ActiveProfile.PROD.equals(profileName)) {
				errorResponse.removeStackTrace();
			}
		}
		return new ResponseEntity<>(errorResponse, HttpStatus.valueOf(errorResponse.getStatus()));
	}

	record RequestLogging(
			String requestMethod,
			String requestUrl,
			Map<String, Object> requestHeaders,
			String requestQueryString,
			Map<String, Object> requestBodyData,
			String refererUrl
	) {
	}
}
