package com.postion.airlineorderbackend.dto;

import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Getter;

/**
 * API响应封装类 使用了泛型 T，可以容纳任何类型的数据
 * 
 */
@Getter
@AllArgsConstructor(access = AccessLevel.PRIVATE)
public class ApiResponse<T> {

	// 成功的标志
	private final boolean success;

	// 业务错误代码（成功时为null）
	private final String code;

	// 响应消息
	private final String message;

	// 响应数据
	private final T data;

	// 响应成功（带数据）
	public static <T> ApiResponse<T> success(T data) {
		return new ApiResponse<T>(true, "200", "操作成功", data);
	}

	// 响应成功（不带数据）
	public static <T> ApiResponse<T> success() {
		return new ApiResponse<T>(true, "200", "操作成功", null);
	}

	// 响应失败
	public static <T> ApiResponse<T> error(String code, String message) {
		return new ApiResponse<T>(false, code, message, null);
	}

}
