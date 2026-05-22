package cn.yq.system.domain.vo.req;

import jakarta.validation.constraints.NotBlank;

public record PermissionCreateReq(
        Long parentId,
        @NotBlank String permissionCode,
        @NotBlank String permissionName,
        @NotBlank String permissionType,
        String path,
        String component,
        String description,
        Integer sortOrder,
        Integer status
) {
}


