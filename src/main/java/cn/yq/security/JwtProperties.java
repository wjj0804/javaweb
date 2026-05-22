package cn.yq.security;

import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.stereotype.Component;

/**
 * JWT 配置项，统一管理密钥、过期时间和请求头名称。
 */
@Component
@ConfigurationProperties(prefix = "yq.jwt")
public class JwtProperties {
    private String secret = "yq-ai-admin-default-secret";
    private long expireSeconds = 7200;
    private String header = "Authorization";

    public String getSecret() {
        return secret;
    }

    public void setSecret(String secret) {
        this.secret = secret;
    }

    public long getExpireSeconds() {
        return expireSeconds;
    }

    public void setExpireSeconds(long expireSeconds) {
        this.expireSeconds = expireSeconds;
    }

    public String getHeader() {
        return header;
    }

    public void setHeader(String header) {
        this.header = header;
    }
}
