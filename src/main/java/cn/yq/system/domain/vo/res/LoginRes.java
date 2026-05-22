package cn.yq.system.domain.vo.res;

import java.util.List;

/**
 * 登录响应结果，前端登录成功后直接保存 token、用户基础信息、角色编码和权限编码。
 */
public record LoginRes(
        String token,
        Long expiresIn,
        Long userId,
        String username,
        String nickname,
        List<String> roleCodes,
        List<String> permissionCodes
) {
}
