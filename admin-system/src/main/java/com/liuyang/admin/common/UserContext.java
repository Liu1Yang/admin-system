package com.liuyang.admin.common;

public final class UserContext {

    private static final ThreadLocal<Long> USER_ID = new ThreadLocal<>();

    private UserContext() {
    }

    public static void setUserId(Long userId) {  // Interceptor 里存
        USER_ID.set(userId);
    }

    public static Long getUserId() {  // Controller 里取，如 /api/auth/me
        return USER_ID.get();
    }

    public static void clear() {   // 请求结束清掉
        USER_ID.remove();
    }
}
