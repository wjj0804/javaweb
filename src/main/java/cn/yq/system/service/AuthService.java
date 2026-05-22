package cn.yq.system.service;

import cn.yq.system.domain.vo.req.LoginReq;
import cn.yq.system.domain.vo.res.LoginRes;

/**
 * 登录认证业务接口，负责校验账号密码并签发访问令牌。
 */
public interface AuthService {
    LoginRes login(LoginReq request);
}
