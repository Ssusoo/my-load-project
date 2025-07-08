package com.me.ssu.myloadproject.global.dto;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonPropertyOrder;
import com.fasterxml.jackson.core.JsonEncoding;
import com.google.common.base.Throwables;
import com.me.ssu.myloadproject.global.constant.ApiResponseCode;
import com.me.ssu.myloadproject.global.util.ConverterUtil;
import jakarta.servlet.http.HttpServletResponse;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.validation.BindingResult;
import org.springframework.web.method.annotation.MethodArgumentTypeMismatchException;

import java.io.IOException;
import java.io.PrintWriter;
import java.io.StringWriter;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@JsonPropertyOrder({ "code", "message", "violations", "fieldErrors", "rootCause" })
@Slf4j
public class ErrorResponse {
	@JsonIgnore
	private int status;
	private String message;

	@JsonInclude(JsonInclude.Include.NON_NULL)
	private Violations violations;

	@JsonInclude(JsonInclude.Include.NON_NULL)
	private List<FieldError> fieldErrors;

	private String code;

	@JsonInclude(JsonInclude.Include.NON_NULL)
	private Map<String, Object> rootCause;

	@JsonInclude(JsonInclude.Include.NON_NULL)
	private String stackTrace;

	private ErrorResponse(final ApiResponseCode code, final List<FieldError> errors) {
		this.message = code.getMessage();
		this.status = code.getStatus();
		this.fieldErrors = errors;
		this.code = code.getCode();
	}

	private ErrorResponse(final ApiResponseCode code) {
		this.message = code.getMessage();
		this.status = code.getStatus();
		this.code = code.getCode();
		this.fieldErrors = null;
	}

	private ErrorResponse(final ApiResponseCode code, final String message) {
		this.message = message;
		this.status = code.getStatus();
		this.code = code.getCode();
		this.fieldErrors = null;
	}

	private ErrorResponse(final ApiResponseCode code, final String message, final Violations violations) {
		this.message = message;
		this.status = code.getStatus();
		this.code = code.getCode();
		this.violations = violations;
		this.fieldErrors = null;
	}

	private ErrorResponse(final ApiResponseCode code, Exception e) {
		var rootCauseMap = ConverterUtil.convertObjectToMap(Throwables.getRootCause(e));
		rootCauseMap.remove("stackTrace"); // 불필요한 정보는 제거한다.

		StringWriter sw = new StringWriter();
		PrintWriter pw = new PrintWriter(sw);
		e.printStackTrace(pw);

		this.message = code.getMessage();
		this.status = code.getStatus();
		this.code = code.getCode();
		this.fieldErrors = null;
		this.rootCause = rootCauseMap;
		this.stackTrace = sw.toString();
	}

	public void removeStackTrace() {
		this.stackTrace = null;
		this.rootCause = null;
	}

	public static ErrorResponse of(final ApiResponseCode code, final BindingResult bindingResult) {
		return new ErrorResponse(code, FieldError.of(bindingResult));
	}

	public static ErrorResponse of(final ApiResponseCode code) {
		return new ErrorResponse(code);
	}

	public static ErrorResponse of(final ApiResponseCode code, final String message) {
		return new ErrorResponse(code, message);
	}

	public static ErrorResponse of(final ApiResponseCode code, final String message, Violations violations) {
		return new ErrorResponse(code, message, violations);
	}

	public static ErrorResponse of(MethodArgumentTypeMismatchException e) {
		List<ErrorResponse.FieldError> errors = null;
		var value = e.getValue();
		if (value != null) {
			errors = ErrorResponse.FieldError.of(e.getName(), value.toString(), e.getErrorCode());
		}

		return new ErrorResponse(ApiResponseCode.INVALID_TYPE_VALUE, errors);
	}

	public static ErrorResponse of(Exception e) {
		return new ErrorResponse(ApiResponseCode.INTERNAL_SERVER_ERROR, e);
	}

	public static void setErrorResponse(
			HttpServletResponse response,
			ApiResponseCode apiResponseCode,
			ErrorResponse errorResponse) throws IOException {
		try {
			var responseBody = ConverterUtil.convertObjectToJson(errorResponse);

			response.setStatus(apiResponseCode.getStatus());
			response.getOutputStream().write(responseBody.getBytes(JsonEncoding.UTF8.getJavaName()));
		} catch (Exception ex) {
			log.error("setErrorResponse", ex);

			response.setStatus(apiResponseCode.getStatus());
			response.setHeader("Content-Type", "application/json;charset=utf-8");
			response.getOutputStream().write(ConverterUtil.convertObjectToJson(ErrorResponse.of(ex)).getBytes(JsonEncoding.UTF8.getJavaName()));
		}
	}

	@Getter
	@NoArgsConstructor(access = AccessLevel.PROTECTED)
	public static class FieldError {
		private String field;

		@JsonInclude(JsonInclude.Include.NON_NULL)
		private String value;
		private String reason;

		private FieldError(final String field, final String value, final String reason) {
			this.field = field;
			this.value = value;
			this.reason = reason;
		}

		public static List<FieldError> of(final String field, final String value, final String reason) {
			var fieldErrors = new ArrayList<FieldError>();
			fieldErrors.add(new FieldError(field, value, reason));
			return fieldErrors;
		}

		private static List<FieldError> of(final BindingResult bindingResult) {
			final var fieldErrors = bindingResult.getFieldErrors();
			return fieldErrors.stream()
					.map(error -> new FieldError(error.getField(),
							error.getRejectedValue() == null ? "" : error.getRejectedValue().toString(),
							error.getDefaultMessage()))
					.collect(Collectors.toList());
		}
	}
}
