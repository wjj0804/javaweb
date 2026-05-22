package cn.yq.security;

/**
 * 当前登录用户上下文，拦截器解析 JWT 后写入，业务层需要时可直接读取。
 */
public final class LoginUserContext {
    private static final ThreadLocal<Long> USER_ID_HOLDER = new ThreadLocal<>();

    private LoginUserContext() {
    }

    public static void setUserId(Long userId) {
        // 每个请求线程单独保存当前用户 ID，避免在方法参数里层层传递登录用户。
        USER_ID_HOLDER.set(userId);
    }

    public static Long getUserId() {
        // 未登录或当前请求未经过 JWT 拦截器时返回 null。
        return USER_ID_HOLDER.get();
    }

    public static void clear() {
        // 请求完成后清除，避免线程池复用时把上一次请求的用户 ID 带到下一次请求。
        USER_ID_HOLDER.remove();
    }
}
