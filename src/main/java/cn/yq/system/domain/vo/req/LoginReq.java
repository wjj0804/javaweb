package cn.yq.system.domain.vo.req;

import jakarta.validation.constraints.NotBlank;

/**
 * 登录请求参数，前端提交用户名和密码后换取 JWT 访问令牌。
 */
public record LoginReq(
        @NotBlank(message = "用户名不能为空")
        String username,

        @NotBlank(message = "密码不能为空")
        String password
) {
}
