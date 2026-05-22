package cn.yq.system.controller;

import cn.yq.common.ApiResponse;
import cn.yq.system.domain.vo.req.LoginReq;
import cn.yq.system.domain.vo.res.LoginRes;
import cn.yq.system.service.AuthService;
import jakarta.validation.Valid;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

/**
 * 登录认证接口，登录成功后返回 Bearer JWT。
 */
@RestController
@RequestMapping("/api/auth")
public class AuthController {
    private final AuthService authService;

    public AuthController(AuthService authService) {
        this.authService = authService;
    }

    /**
     * 用户登录接口，前端拿到 token 后放入 Authorization 请求头访问后台接口。
     */
    @PostMapping("/login")
    public ApiResponse<LoginRes> login(@Valid @RequestBody LoginReq request) {
        return ApiResponse.ok(authService.login(request));
    }
}
