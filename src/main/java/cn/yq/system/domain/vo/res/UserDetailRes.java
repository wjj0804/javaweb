package cn.yq.system.domain.vo.res;

import java.util.Date;
import java.util.List;

public record UserDetailRes(
        Long id,
        String username,
        String nickname,
        String phone,
        String email,
        Integer status,
        List<RoleDetailRes> roles,
        List<PermissionDetailRes> permissions,
        Date createdAt,
        Date updatedAt
) {
}


