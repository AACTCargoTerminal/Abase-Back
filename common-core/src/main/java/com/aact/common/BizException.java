package com.aact.common;

public class BizException extends RuntimeException {
	private final String type;

	public BizException(String type, String message) {
		super(message);
		this.type = type;
	}

	public BizException(String type, String message, Throwable cause) {
		super(message, cause);
		this.type = type;
	}

	public String getType() {
		return type;
	}
}
