package cn.yq.system.domain.vo.req;

import java.util.List;

public record RoleUpdateReq(
        String roleName,
        String description,
        Integer status,
        List<Long> permissionIds
) {
}


