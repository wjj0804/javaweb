package cn.yq.system.domain.entity;

import java.util.Date;

/**
 * 用户角色关联实体，对应 sys_user_role 表。
 */
public class SysUserRoleEntity {
    /**
     * 主键 ID。
     */
    private Long id;
    /**
     * 用户 ID。
     */
    private Long userId;
    /**
     * 角色 ID。
     */
    private Long roleId;
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

    public Long getUserId() {
        return userId;
    }

    public void setUserId(Long userId) {
        this.userId = userId;
    }

    public Long getRoleId() {
        return roleId;
    }

    public void setRoleId(Long roleId) {
        this.roleId = roleId;
    }

    public Date getCreatedAt() {
        return createdAt;
    }

    public void setCreatedAt(Date createdAt) {
        this.createdAt = createdAt;
    }
}
