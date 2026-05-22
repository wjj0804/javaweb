package cn.yq.system.domain.vo.res;

import java.util.Date;
import java.util.List;

public record RoleDetailRes(
        Long id,
        String roleCode,
        String roleName,
        String description,
        Integer status,
        List<PermissionDetailRes> permissions,
        Date createdAt,
        Date updatedAt
) {
}


