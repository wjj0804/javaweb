package cn.yq.system.domain.entity;

import java.util.Date;

/**
 * 角色权限关联实体，对应 sys_role_permission 表。
 */
public class SysRolePermissionEntity {
    /**
     * 主键 ID。
     */
    private Long id;
    /**
     * 角色 ID。
     */
    private Long roleId;
    /**
     * 权限 ID。
     */
    private Long permissionId;
    /**
     * 创建时间。
     */
    private Date createdAt;

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public Long getRoleId() {
        return roleId;
    }

    public void setRoleId(Long roleId) {
        this.roleId = roleId;
    }

    public Long getPermissionId() {
        return permissionId;
    }

    public void setPermissionId(Long permissionId) {
        this.permissionId = permissionId;
    }

    public Date getCreatedAt() {
        return createdAt;
    }

    public void setCreatedAt(Date createdAt) {
        this.createdAt = createdAt;
    }
}
