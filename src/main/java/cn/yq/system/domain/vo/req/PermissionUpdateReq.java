package cn.yq.system.domain.vo.req;

public record PermissionUpdateReq(
        Long parentId,
        String permissionName,
        String permissionType,
        String path,
        String component,
        String description,
        Integer sortOrder,
        Integer status
) {
}


