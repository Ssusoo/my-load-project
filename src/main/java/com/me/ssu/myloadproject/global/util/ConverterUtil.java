package com.me.ssu.myloadproject.global.util;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.DeserializationFeature;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.SerializationFeature;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;
import lombok.AccessLevel;
import lombok.NoArgsConstructor;
import org.springframework.util.StringUtils;

import java.io.IOException;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@NoArgsConstructor(access = AccessLevel.PRIVATE)
@SuppressWarnings({"unchecked", "unused"})
public class ConverterUtil {

	private static final ObjectMapper objectMapper = new ObjectMapper()
			.registerModule(new JavaTimeModule())
			.configure(SerializationFeature.WRITE_DATES_AS_TIMESTAMPS, false)
			.configure(DeserializationFeature.ADJUST_DATES_TO_CONTEXT_TIME_ZONE,false);

	public static String convertObjectToJson(Object object) throws JsonProcessingException {
		return objectMapper.writeValueAsString(object);
	}

	public static Map<String, Object> convertJsonToMap(String jsonValue) throws JsonProcessingException {
		objectMapper.registerModule(new JavaTimeModule());
		if (StringUtils.hasLength(jsonValue)) {
			return objectMapper.readValue(jsonValue, Map.class);
		} else {
			return null;
		}
	}

	public static Map<String, Object> convertObjectToMap(Object object) {
		try {
			return convertJsonToMap(convertObjectToJson(object));
		} catch (Exception e) {
			return new HashMap<>();
		}
	}

	public static Map<String, Object> convertJsonToMap(byte[] jsonValue) throws IOException {
		if (jsonValue.length > 0) {
			return objectMapper.readValue(jsonValue, Map.class);
		} else {
			return null;
		}
	}

	public static <T> T convertJsonToListGeneric(String jsonValue, Class<?> target)
			throws JsonProcessingException, ClassNotFoundException {
		if (StringUtils.hasLength(jsonValue)) {
			return objectMapper.readValue(jsonValue,
					objectMapper.getTypeFactory().constructCollectionType(List.class, Class.forName(target.getName())));
		} else {
			return null;
		}
	}

	public static <T> T convertJsonToGeneric(String jsonValue, Class<T> target)
			throws JsonProcessingException, ClassNotFoundException {
		if (StringUtils.hasLength(jsonValue)) {
			return objectMapper.readValue(jsonValue,
					objectMapper.getTypeFactory().constructType(Class.forName(target.getName())));
		} else {
			return null;
		}
	}

	public static <T> T convertMapToGeneric(Map<String, Object> map, Class<T> target) {
		return objectMapper.convertValue(map, target);
	}
}
