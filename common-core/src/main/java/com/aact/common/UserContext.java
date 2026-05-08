package com.aact.common;

public class UserContext {

	private static final ThreadLocal<ClsUserInfo> USER_HOLDER = new ThreadLocal<>();

	public static void set(ClsUserInfo user) {
		USER_HOLDER.set(user);
	}

	public static ClsUserInfo get() {
		return USER_HOLDER.get();
	}

	public static void clear() {
		USER_HOLDER.remove();
	}
}