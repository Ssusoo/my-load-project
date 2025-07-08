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
import org.springframework.security.access.AccessDeniedException;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.authentication.InsufficientAuthenticationException;
import org.springframework.security.authentication.InternalAuthenticationServiceException;
import org.springframework.security.core.AuthenticationException;
import org.springframework.validation.BindException;
import org.springframework.web.HttpMediaTypeNotSupportedException;
import org.springframework.web.HttpRequestMethodNotSupportedException;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import org.springframework.web.method.annotation.MethodArgumentTypeMismatchException;
import org.springframework.web.servlet.NoHandlerFoundException;

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
	 * 인증(Authentication) 관련
	 */
	/**
	 * {
	 *   "code": "LOGIN_FAILURE",
	 *   "message": "아이디 또는 비밀번호가 올바르지 않습니다."
	 * }
	 */
	/**
	 * {
	 *   "code": "UNAUTHORIZED",
	 *   "message": "인증이 필요한 요청입니다."
	 * }
	 */
	@ExceptionHandler({
			InsufficientAuthenticationException.class,      // 인증 정보 부족(ex : 로그인 안 하고 접근한 경우)
			BadCredentialsException.class,                  // 잘못된 아이디 / 패스워드
			InternalAuthenticationServiceException.class,   // 로그인 시 내부 서비스 오류(ex : 유저 조회 실패)
			AuthenticationException.class })                // 인증 관련 기본 예외(위 예외들의 부모)
	protected ResponseEntity<ErrorResponse> handleUnAuthenticationException(AuthenticationException ex) {
		var errorCode = ApiResponseCode.UNAUTHORIZED; // 기본 401
		if (ex instanceof InternalAuthenticationServiceException || ex instanceof BadCredentialsException) {
			errorCode = ApiResponseCode.LOGIN_FAILURE; // 로그인 실패로 세분화 처리
		}
		return setResponse(ErrorResponse.of(errorCode));
	}

	/**
	 * 인가(Authorization) 관련 : 사용자가 로그인해도 권한이 없는 경우
	 *  1) 일반 사용자가 어드민 권한이 필요한 URL 접근할 때
	 *  2) 로그인은 했으나 리소스에 접근할 권한이 없을 때
	 */
	/**
	 * {
	 *   "code": "ACCESS_DENIED",
	 *   "message": "접근 권한이 없습니다."
	 * }
	 */
	@ExceptionHandler(AccessDeniedException.class)
	protected ResponseEntity<ErrorResponse> handleAccessDeniedException(AccessDeniedException ex) {
		return setResponse(ErrorResponse.of(ApiResponseCode.ACCESS_DENIED));
	}

	/**
	 * HTTP 요청 오류 관련 : POST / GET / PUT / DELETE
	 *  Post 요청인데, Get 요청으로 잘못 보낸 경우
	 */
	@ExceptionHandler(HttpRequestMethodNotSupportedException.class)
	protected ResponseEntity<ErrorResponse> handleHttpRequestMethodNotSupportedException(HttpRequestMethodNotSupportedException ex) {
		return setResponse(ErrorResponse.of(ApiResponseCode.METHOD_NOT_ALLOWED));
	}

	/**
	 * HTTP 요청 오류 관련 : 예상 밖의 요청으로 응답 받을 때
	 *  ex) application/json(Json 요청을 기대했으나) -> text/xml, text/plain(다른 요청일 경우)
	 */
	/**
	 * {
	 *   "code": "UNSUPPORTED_MEDIA_TYPE",
	 *   "message": "지원하지 않는 Content-Type 입니다."
	 * }
	 */
	@ExceptionHandler(HttpMediaTypeNotSupportedException.class)
	protected ResponseEntity<ErrorResponse> handleHttpMediaTypeNotSupportedException() {
		return setResponse(ErrorResponse.of(ApiResponseCode.UNSUPPORTED_MEDIA_TYPE));
	}

	/**
	 * HTTP 요청 오류 관련 : URL에 대앙하는 핸들러(메서드)가 없을 때
	 *  해당 URL을 처리하는 컨트롤러가 없을 경우
	 */
	@ExceptionHandler(NoHandlerFoundException.class)
	protected ResponseEntity<ErrorResponse> handleNoHandlerFoundException() {
		return setResponse(ErrorResponse.of(ApiResponseCode.NOT_FOUND));
	}

	/**
	 * 비즈니스 로직 요청 오류 관련 : 기존 예외처리 말고 커스텀 비즈니스 오류 요청 처리
	 */
	@ExceptionHandler({ BusinessRuntimeException.class})
	protected ResponseEntity<ErrorResponse> handleBusinessException(final BusinessRuntimeException ex) {
		return setResponse(ErrorResponse.of(
				ex.getErrorCode(),      // 에러 코드
				ex.getErrorMessage(),   // 에러 메시지
				ex.getViolations())     // 상세한 오류 정보
		);
	}

	/**
	 */
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
