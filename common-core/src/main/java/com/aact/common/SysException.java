package com.aact.common;

public class SysException extends RuntimeException {
	private final String type;

	public SysException(String type, String message) {
		super(message);
		this.type = type;
	}

	public SysException(String type, String message, Throwable cause) {
		super(message, cause);
		this.type = type;
	}

	public String getType() {
		return type;
	}
}