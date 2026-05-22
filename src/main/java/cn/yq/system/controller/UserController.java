package cn.yq.system.controller;

import cn.yq.common.ApiResponse;
import cn.yq.common.PageResult;
import cn.yq.system.domain.vo.req.AssignRoleReq;
import cn.yq.system.domain.vo.req.UserCreateReq;
import cn.yq.system.domain.vo.res.UserDetailRes;
import cn.yq.system.domain.vo.req.UserQueryReq;
import cn.yq.system.domain.vo.req.UserUpdateReq;
import cn.yq.system.service.UserService;
import jakarta.validation.Valid;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/system/users")
/**
 * 系统用户接口，提供用户分页查询、详情、新增、编辑、删除和角色分配能力。
 */
public class UserController {
    private final UserService userService;

    public UserController(UserService userService) {
        this.userService = userService;
    }

    @GetMapping
    /**
     * 分页查询用户列表，支持按用户名或昵称模糊搜索。
     */
    public ApiResponse<PageResult<UserDetailRes>> list(UserQueryReq request) {
        return ApiResponse.ok(userService.list(request.getKeyword(), request.getPageNum(), request.getPageSize()));
    }

    @GetMapping("/{id}")
    /**
     * 根据用户 ID 查询用户详情，返回用户角色和最终权限集合。
     */
    public ApiResponse<UserDetailRes> get(@PathVariable Long id) {
        return ApiResponse.ok(userService.get(id));
    }

    @PostMapping
    /**
     * 创建后台用户，创建后可同时绑定角色。
     */
    public ApiResponse<UserDetailRes> create(@Valid @RequestBody UserCreateReq request) {
        return ApiResponse.ok(userService.create(request));
    }

    @PutMapping("/{id}")
    /**
     * 更新用户基础信息，传入 roleIds 时同步重置用户角色。
     */
    public ApiResponse<UserDetailRes> update(@PathVariable Long id, @Valid @RequestBody UserUpdateReq request) {
        return ApiResponse.ok(userService.update(id, request));
    }

    @DeleteMapping("/{id}")
    /**
     * 删除用户，并清理用户角色关联。
     */
    public ApiResponse<Void> delete(@PathVariable Long id) {
        userService.delete(id);
        return ApiResponse.ok();
    }

    @PutMapping("/{id}/roles")
    /**
     * 重置用户角色关系，用于单独维护用户拥有的角色集合。
     */
    public ApiResponse<Void> resetRoles(@PathVariable Long id, @Valid @RequestBody AssignRoleReq request) {
        userService.resetRoles(id, request.roleIds());
        return ApiResponse.ok();
    }
}

