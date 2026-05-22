package cn.yq.config;

import org.springframework.context.annotation.Configuration;
import org.springframework.web.servlet.config.annotation.CorsRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

/**
 * 全局跨域配置，允许后台前端在本地开发时调用后端接口。
 */
@Configuration
public class CorsConfig implements WebMvcConfigurer {
    private static final String[] ALLOWED_ORIGIN_PATTERNS = {
            "http://localhost:*",
            "http://127.0.0.1:*",
            "http://192.168.*.*:*"
    };

    @Override
    public void addCorsMappings(CorsRegistry registry) {
        registry.addMapping("/**")
                .allowedOriginPatterns(ALLOWED_ORIGIN_PATTERNS)
                .allowedMethods("GET", "POST", "PUT", "DELETE", "PATCH", "OPTIONS")
                .allowedHeaders("*")
                .exposedHeaders("*")
                .allowCredentials(true)
                .maxAge(3600);
    }
}
