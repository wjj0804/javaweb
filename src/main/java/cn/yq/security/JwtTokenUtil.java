package cn.yq.security;

import cn.hutool.core.date.DateUtil;
import cn.hutool.jwt.JWT;
import cn.hutool.jwt.JWTUtil;
import cn.hutool.jwt.JWTValidator;
import org.springframework.stereotype.Component;
import org.springframework.util.StringUtils;

import java.nio.charset.StandardCharsets;
import java.util.Date;

/**
 * Hutool JWT 工具类，负责令牌签发、解析和有效期校验。
 */
@Component
public class JwtTokenUtil {
    private static final String USER_ID = "userId";
    private static final String USERNAME = "username";

    private final JwtProperties properties;

    public JwtTokenUtil(JwtProperties properties) {
        this.properties = properties;
    }

    /**
     * 根据用户主键和用户名生成 JWT，过期时间由配置文件控制。
     */
    public String createToken(Long userId, String username) {
        // 计算 token 的过期时间，后续拦截器会基于该时间判断登录是否失效。
        Date expiresAt = DateUtil.offsetSecond(new Date(), Math.toIntExact(properties.getExpireSeconds()));
        // Hutool JWT 默认把 setPayload 写入 JWT claims，这里只放必要身份信息，避免泄露密码等敏感字段。
        return JWT.create()
                .setPayload(USER_ID, userId)
                .setPayload(USERNAME, username)
                .setIssuedAt(new Date())
                .setExpiresAt(expiresAt)
                .setKey(secretBytes())
                .sign();
    }

    /**
     * 校验 JWT 签名和过期时间，通过后返回用户 ID。
     */
    public Long parseUserId(String token) {
        // 先校验 token 是否为空以及签名是否正确，签名错误说明 token 被篡改或密钥不一致。
        if (!StringUtils.hasText(token) || !JWTUtil.verify(token, secretBytes())) {
            return null;
        }
        JWT jwt = JWTUtil.parseToken(token);
        // 校验 exp 过期时间，过期后会抛出异常，由拦截器统一转换为 401 响应。
        JWTValidator.of(jwt).validateDate(new Date());
        // 拿到登录时写入的用户 ID，供拦截器写入当前线程上下文。
        Object userId = jwt.getPayload(USER_ID);
        return userId == null ? null : Long.valueOf(userId.toString());
    }

    public long getExpireSeconds() {
        return properties.getExpireSeconds();
    }

    private byte[] secretBytes() {
        // Hutool JWT 使用字节数组作为 HMAC 签名密钥。
        return properties.getSecret().getBytes(StandardCharsets.UTF_8);
    }
}
