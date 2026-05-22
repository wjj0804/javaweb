package cn.yq.system.domain.vo.res;

import java.util.Date;
import java.util.List;

/**
 * 权限详情返回体，children 用于前端树形展示菜单、按钮和 API 权限。
 */
public record PermissionDetailRes(
        Long id,
        Long parentId,
        String permissionCode,
        String permissionName,
        String permissionType,
        String path,
        String component,
        String description,
        Integer sortOrder,
        Integer status,
        Date createdAt,
        Date updatedAt,
        List<PermissionDetailRes> children
) {
}


