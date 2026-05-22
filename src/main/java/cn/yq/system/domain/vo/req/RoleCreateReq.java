package cn.yq.system.domain.vo.req;

import jakarta.validation.constraints.NotBlank;

import java.util.List;

public record RoleCreateReq(
        @NotBlank String roleCode,
        @NotBlank String roleName,
        String description,
        Integer status,
        List<Long> permissionIds
) {
}


