package cn.yq.system.service.impl;

import cn.yq.exception.BusinessException;
import cn.yq.security.JwtTokenUtil;
import cn.yq.system.domain.entity.SysPermissionEntity;
import cn.yq.system.domain.entity.SysRoleEntity;
import cn.yq.system.domain.entity.SysUserEntity;
import cn.yq.system.domain.vo.req.LoginReq;
import cn.yq.system.domain.vo.res.LoginRes;
import cn.yq.system.mapper.SysPermissionEntityMapper;
import cn.yq.system.mapper.SysRoleEntityMapper;
import cn.yq.system.mapper.SysUserEntityMapper;
import cn.yq.system.service.AuthService;
import org.springframework.stereotype.Service;

import java.util.List;

/**
 * 登录认证业务实现，负责账号密码校验、角色权限查询和 JWT 签发。
 */
@Service
public class AuthServiceImpl implements AuthService {
    private final SysUserEntityMapper userMapper;
    private final SysRoleEntityMapper roleMapper;
    private final SysPermissionEntityMapper permissionMapper;
    private final JwtTokenUtil jwtTokenUtil;

    public AuthServiceImpl(
            SysUserEntityMapper userMapper,
            SysRoleEntityMapper roleMapper,
            SysPermissionEntityMapper permissionMapper,
            JwtTokenUtil jwtTokenUtil
    ) {
        this.userMapper = userMapper;
        this.roleMapper = roleMapper;
        this.permissionMapper = permissionMapper;
        this.jwtTokenUtil = jwtTokenUtil;
    }

    @Override
    public LoginRes login(LoginReq request) {
        // 第一步：根据用户名查询账号。登录失败时统一提示，避免暴露账号是否存在。
        SysUserEntity user = userMapper.findByUsername(request.username());
        if (user == null || !request.password().equals(user.getPassword())) {
            throw new BusinessException("用户名或密码错误");
        }

        // 第二步：校验账号状态，禁用账号不允许登录，也不会签发 JWT。
        if (user.getStatus() == null || user.getStatus() != 1) {
            throw new BusinessException("账号已被禁用，请联系管理员");
        }

        // 第三步：查询当前账号绑定的角色，前端可用 roleCodes 做角色级判断。
        List<String> roleCodes = roleMapper.findByUserId(user.getId()).stream()
                .filter(role -> role.getStatus() != null && role.getStatus() == 1)
                .map(SysRoleEntity::getRoleCode)
                .distinct()
                .toList();

        // 第四步：查询当前账号通过角色继承到的最终权限，前端可用 permissionCodes 控制菜单和按钮。
        List<String> permissionCodes = permissionMapper.findByUserId(user.getId()).stream()
                .filter(permission -> permission.getStatus() != null && permission.getStatus() == 1)
                .map(SysPermissionEntity::getPermissionCode)
                .distinct()
                .toList();

        // 第五步：账号、角色、权限都处理完成后签发 JWT，后续请求放入 Authorization 请求头。
        String token = jwtTokenUtil.createToken(user.getId(), user.getUsername());
        return new LoginRes(
                token,
                jwtTokenUtil.getExpireSeconds(),
                user.getId(),
                user.getUsername(),
                user.getNickname(),
                roleCodes,
                permissionCodes
        );
    }
}
