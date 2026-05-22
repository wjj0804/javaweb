package cn.yq.common;

import java.util.Date;

public record ApiResponse<T>(
        int code,
        String message,
        T data,
        Date timestamp
) {
    public static <T> ApiResponse<T> ok(T data) {
        return new ApiResponse<>(0, "success", data, new Date());
    }

    public static ApiResponse<Void> ok() {
        return ok(null);
    }

    public static ApiResponse<Void> fail(int code, String message) {
        return new ApiResponse<>(code, message, null, new Date());
    }
}

