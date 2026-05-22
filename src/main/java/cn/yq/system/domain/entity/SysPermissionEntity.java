package cn.yq.system.domain.entity;

import java.util.Date;

/**
 * 系统权限实体，对应 sys_permission 表，菜单和按钮统一作为权限节点维护。
 */
public class SysPermissionEntity {
    /**
     * 主键 ID。
     */
    private Long id;
    /**
     * 父级权限 ID，0 表示根节点。
     */
    private Long parentId;
    /**
     * 权限编码，全局唯一。
     */
    private String permissionCode;
    /**
     * 权限名称，用于页面展示。
     */
    private String permissionName;
    /**
     * 权限类型：MENU 菜单，BUTTON 按钮。
     */
    private String permissionType;
    /**
     * 前端路由路径。
     */
    private String path;
    /**
     * 前端组件路径。
     */
    private String component;
    /**
     * 权限描述，用于说明菜单、按钮或 API 的使用场景。
     */
    private String description;
    /**
     * 排序值，数值越小越靠前。
     */
    private Integer sortOrder;
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

    public Long getParentId() {
        return parentId;
    }

    public void setParentId(Long parentId) {
        this.parentId = parentId;
    }

    public String getPermissionCode() {
        return permissionCode;
    }

    public void setPermissionCode(String permissionCode) {
        this.permissionCode = permissionCode;
    }

    public String getPermissionName() {
        return permissionName;
    }

    public void setPermissionName(String permissionName) {
        this.permissionName = permissionName;
    }

    public String getPermissionType() {
        return permissionType;
    }

    public void setPermissionType(String permissionType) {
        this.permissionType = permissionType;
    }

    public String getPath() {
        return path;
    }

    public void setPath(String path) {
        this.path = path;
    }

    public String getComponent() {
        return component;
    }

    public void setComponent(String component) {
        this.component = component;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public Integer getSortOrder() {
        return sortOrder;
    }

    public void setSortOrder(Integer sortOrder) {
        this.sortOrder = sortOrder;
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

