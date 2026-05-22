package cn.yq.security;

import cn.yq.common.ApiResponse;
import com.fasterxml.jackson.databind.ObjectMapper;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Component;
import org.springframework.web.servlet.HandlerInterceptor;

import java.nio.charset.StandardCharsets;

/**
 * JWT 登录校验拦截器，保护后台 API，未登录或令牌失效时直接返回 401。
 */
@Component
public class JwtAuthInterceptor implements HandlerInterceptor {
    private static final String BEARER_PREFIX = "Bearer ";

    private final JwtProperties properties;
    private final JwtTokenUtil jwtTokenUtil;
    private final ObjectMapper objectMapper;

    public JwtAuthInterceptor(JwtProperties properties, JwtTokenUtil jwtTokenUtil, ObjectMapper objectMapper) {
        this.properties = properties;
        this.jwtTokenUtil = jwtTokenUtil;
        this.objectMapper = objectMapper;
    }

    @Override
    public boolean preHandle(HttpServletRequest request, HttpServletResponse response, Object handler) throws Exception {
        // 浏览器跨域预检请求不携带业务 token，直接放行给 CORS 配置处理。
        if ("OPTIONS".equalsIgnoreCase(request.getMethod())) {
            return true;
        }
        // 从请求头中提取 JWT，支持 Authorization: Bearer xxx 的标准格式。
        String token = resolveToken(request);
        try {
            // 解析并校验 token，失败时返回 null 或抛出异常，统一走 401 响应。
            Long userId = jwtTokenUtil.parseUserId(token);
            if (userId == null) {
                writeUnauthorized(response, "登录已过期，请重新登录");
                return false;
            }
            // 把当前登录用户 ID 放入 ThreadLocal，后续业务方法可通过 LoginUserContext 获取。
            LoginUserContext.setUserId(userId);
            return true;
        } catch (Exception ex) {
            // token 过期、格式错误、签名异常都按未登录处理，避免把底层异常暴露给前端。
            writeUnauthorized(response, "登录已过期，请重新登录");
            return false;
        }
    }

    @Override
    public void afterCompletion(HttpServletRequest request, HttpServletResponse response, Object handler, Exception ex) {
        // 请求结束必须清理 ThreadLocal，避免 Tomcat 线程复用导致用户信息串号。
        LoginUserContext.clear();
    }

    /**
     * 支持标准 Bearer Token，也兼容直接传 token 的 Authorization 请求头。
     */
    private String resolveToken(HttpServletRequest request) {
        String headerValue = request.getHeader(properties.getHeader());
        if (headerValue == null) {
            return null;
        }
        if (headerValue.startsWith(BEARER_PREFIX)) {
            // 去掉 Bearer 前缀，只保留真正的 JWT 字符串。
            return headerValue.substring(BEARER_PREFIX.length());
        }
        // 兼容前端直接把 token 放到 Authorization 请求头里的情况。
        return headerValue;
    }

    private void writeUnauthorized(HttpServletResponse response, String message) throws Exception {
        // 拦截器中不能直接返回 ApiResponse，所以手动写 JSON 响应体。
        response.setStatus(HttpServletResponse.SC_UNAUTHORIZED);
        response.setCharacterEncoding(StandardCharsets.UTF_8.name());
        response.setHeader(HttpHeaders.CONTENT_TYPE, MediaType.APPLICATION_JSON_VALUE);
        response.getWriter().write(objectMapper.writeValueAsString(ApiResponse.fail(401, message)));
    }
}
