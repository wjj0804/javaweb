package cn.yq.config;

import cn.yq.security.JwtAuthInterceptor;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.servlet.config.annotation.InterceptorRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

/**
 * Web MVC 扩展配置，注册 JWT 拦截器并放行登录和接口文档地址。
 */
@Configuration
public class WebMvcConfig implements WebMvcConfigurer {
    private final JwtAuthInterceptor jwtAuthInterceptor;

    public WebMvcConfig(JwtAuthInterceptor jwtAuthInterceptor) {
        this.jwtAuthInterceptor = jwtAuthInterceptor;
    }

    @Override
    public void addInterceptors(InterceptorRegistry registry) {
        // 只拦截后台 API，静态资源和其他 Spring Boot 内部路径不进入 JWT 校验。
        registry.addInterceptor(jwtAuthInterceptor)
                .addPathPatterns("/api/**")
                // 登录接口和 Swagger 文档必须放行，否则未登录时无法换取 token 或查看接口文档。
                .excludePathPatterns(
                        "/api/auth/login",
                        "/v3/api-docs/**",
                        "/swagger-ui/**",
                        "/swagger-ui.html"
                );
    }
}
