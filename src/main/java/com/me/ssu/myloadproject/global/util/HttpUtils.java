package com.me.ssu.myloadproject.global.util;

import jakarta.servlet.http.HttpServletRequest;
import lombok.AccessLevel;
import lombok.NoArgsConstructor;
import org.apache.commons.lang3.StringUtils;
import org.springframework.web.context.request.RequestAttributes;
import org.springframework.web.context.request.RequestContextHolder;
import org.springframework.web.context.request.ServletRequestAttributes;
import org.springframework.web.util.ContentCachingRequestWrapper;
import org.springframework.web.util.WebUtils;

import java.io.UnsupportedEncodingException;
import java.util.Enumeration;
import java.util.HashMap;
import java.util.Map;

/**
 * HttpServletRequest 관련 기능 추가
 */
@NoArgsConstructor(access = AccessLevel.PRIVATE)
public class HttpUtils {

	/**
	 * Http Request 객체인
	 *  HttpServletRequest를 가져오는 유틸 메서드
	 */
	public static HttpServletRequest getHttpServletRequest() {
		ServletRequestAttributes servletRequestAttributes = (ServletRequestAttributes) RequestContextHolder.getRequestAttributes();

		if (servletRequestAttributes != null) {
			return servletRequestAttributes.getRequest();
		} else {
			return null;
		}
	}

	/**
	 * 요청(Request)의 Body 내용을
	 *  문자열로 읽어오는 유틸 메서드
	 */
	public static String getRequestBody(HttpServletRequest request) {
		// GET 요청이나 단순 form 요청은 Request Body가 없거나 무시 가능이기에 제외
		if (!("application/x-www-form-urlencoded".equalsIgnoreCase(request.getContentType()))
			&& !request.getMethod().equalsIgnoreCase("GET")) {

			// ContentCachingRequestWrapper : 요청 본문을 여러 번 읽을 수 있게 캐싱해주는 래퍼
			// 단, 해당 레퍼를 사용하지 않으면 한번만 있을 수 있음(필터/로그/비즈니스로직 등 여러군데 사용 불가)
			ContentCachingRequestWrapper wrapper = WebUtils.getNativeRequest(request, ContentCachingRequestWrapper.class);

			if (wrapper != null) {
				// 바디 내용을 byte 배열로 가져옴
				byte[] buf = wrapper.getContentAsByteArray();

				if (buf.length > 0) {
					try {
						// 요청 본문을 문자열로 변환해서 반환
						return new String(buf, wrapper.getCharacterEncoding());
					} catch (UnsupportedEncodingException e) {
						return null;
					}
				}
			}
		}
		return null;
	}

	/**
	 * HTTP 요청을 포함한 모든 헤더를 Map<String, Object> 형태로 변환하는 유틸리티 함수
	 */
	/**
	 * http 요청 헤더
	 * GET /example
	 * Host: example.com
	 * User-Agent: PostmanRuntime/7.26.8
	 * Content-Type: application/json
	 */

	/**
	 * Map으로 변환
	 * {
	 *   "host": "example.com",
	 *   "user-agent": "PostmanRuntime/7.26.8",
	 *   "content-type": "application/json"
	 * }
	 */
	public static Map<String, Object> getHeaderToMap(HttpServletRequest request) {
		// 맵 선언
		Map<String, Object> headerMap = null;

		if (request != null) {

			// 요청의 모든 헤더 이름을 가져옴 (Enumeration 형태)
			Enumeration<String> headerNames = request.getHeaderNames();
			String key;
			String value;

			headerMap = new HashMap<>();
			while (headerNames.hasMoreElements()) {
				key = headerNames.nextElement();                    // ex: Content-Type, Authorization
				value = request.getHeader(key);                     // 해당 key의 실제 값
				if (StringUtils.isNoneEmpty(value)) {
					headerMap.put(key.toLowerCase(), value);
				}
			}
		}
		return headerMap;
	}

	/**
	 * Referer 헤더는
	 *  사용자가 어떤 링크나 버튼을 눌러서 이 페이지로 왔는지 나타냄
	 */
	/**
	 * HTTP 요청 헤더
	 *  GET /target-page HTTP/1.1
	 *  Host: www.example.com
	 *  Referer: https://www.google.com/search?q=example
	 */
	public static String getRefererURL(HttpServletRequest request) {
		// referer = "https://www.google.com/search?q=example"
		return request.getHeader("referer");
	}
}
