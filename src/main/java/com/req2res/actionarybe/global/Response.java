package com.req2res.actionarybe.global;

import com.fasterxml.jackson.annotation.JsonInclude;

import lombok.Getter;

@JsonInclude(JsonInclude.Include.NON_NULL)
@Getter
public class Response<T> {
	private boolean success;
	private String message;
	private T data;

	private Response(boolean success, String message, T data) {
		this.success = success;
		this.message = message;
		this.data = data;
	}

	public static <T> Response<T> success(String message, T data) {
		return new Response<>(true, message, data);
	}

	public static <T> Response<T> fail(String message) {
		return new Response<>(false, message, null);
	}
}
