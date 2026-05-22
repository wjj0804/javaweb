package cn.yq.system.domain.entity;

import java.util.Date;

/**
 * 系统角色实体，对应 sys_role 表。
 */
public class SysRoleEntity {
    /**
     * 主键 ID。
     */
    private Long id;
    /**
     * 角色编码，全局唯一。
     */
    private String roleCode;
    /**
     * 角色名称，用于页面展示。
     */
    private String roleName;
    /**
     * 角色描述。
     */
    private String description;
    /**
     * 状态：1 启用，0 禁用。
     */
    private Integer status;
    /**
     * 创建时间。
     */
    private Date createdAt;
    /**
     * 更新时间。
     */
    private Date updatedAt;

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getRoleCode() {
        return roleCode;
    }

    public void setRoleCode(String roleCode) {
        this.roleCode = roleCode;
    }

    public String getRoleName() {
        return roleName;
    }

    public void setRoleName(String roleName) {
        this.roleName = roleName;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public Integer getStatus() {
        return status;
    }

    public void setStatus(Integer status) {
        this.status = status;
    }

    public Date getCreatedAt() {
        return createdAt;
    }

    public void setCreatedAt(Date createdAt) {
        this.createdAt = createdAt;
    }

    public Date getUpdatedAt() {
        return updatedAt;
    }

    public void setUpdatedAt(Date updatedAt) {
        this.updatedAt = updatedAt;
    }
}

