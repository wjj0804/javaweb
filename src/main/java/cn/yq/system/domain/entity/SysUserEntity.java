package cn.yq.system.domain.entity;

import java.util.Date;

/**
 * 系统用户实体，对应 sys_user 表。
 */
public class SysUserEntity {
    /**
     * 主键 ID。
     */
    private Long id;
    /**
     * 登录用户名，全局唯一。
     */
    private String username;
    /**
     * 用户昵称，用于页面展示。
     */
    private String nickname;
    /**
     * 登录密码，保存 BCrypt 加密后的密文。
     */
    private String password;
    /**
     * 手机号。
     */
    private String phone;
    /**
     * 邮箱地址。
     */
    private String email;
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

    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    public String getNickname() {
        return nickname;
    }

    public void setNickname(String nickname) {
        this.nickname = nickname;
    }

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }

    public String getPhone() {
        return phone;
    }

    public void setPhone(String phone) {
        this.phone = phone;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
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

