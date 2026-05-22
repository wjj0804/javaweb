package cn.yq.system.controller;

import cn.yq.common.ApiResponse;
import cn.yq.common.PageResult;
import cn.yq.system.domain.vo.req.AssignPermissionReq;
import cn.yq.system.domain.vo.req.RoleCreateReq;
import cn.yq.system.domain.vo.res.RoleDetailRes;
import cn.yq.system.domain.vo.req.RoleQueryReq;
import cn.yq.system.domain.vo.req.RoleUpdateReq;
import cn.yq.system.service.RoleService;
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
@RequestMapping("/api/system/roles")
/**
 * 系统角色接口，提供角色分页查询、详情、新增、编辑、删除和权限分配能力。
 */
public class RoleController {
    private final RoleService roleService;

    public RoleController(RoleService roleService) {
        this.roleService = roleService;
    }

    @GetMapping
    /**
     * 分页查询角色列表，支持按角色编码或角色名称模糊搜索。
     */
    public ApiResponse<PageResult<RoleDetailRes>> list(RoleQueryReq request) {
        return ApiResponse.ok(roleService.list(request.getKeyword(), request.getPageNum(), request.getPageSize()));
    }

    @GetMapping("/{id}")
    /**
     * 根据角色 ID 查询角色详情，返回该角色已绑定的权限集合。
     */
    public ApiResponse<RoleDetailRes> get(@PathVariable Long id) {
        return ApiResponse.ok(roleService.get(id));
    }

    @PostMapping
    /**
     * 创建角色，创建后可同时绑定权限。
     */
    public ApiResponse<RoleDetailRes> create(@Valid @RequestBody RoleCreateReq request) {
        return ApiResponse.ok(roleService.create(request));
    }

    @PutMapping("/{id}")
    /**
     * 更新角色基础信息，传入 permissionIds 时同步重置角色权限。
     */
    public ApiResponse<RoleDetailRes> update(@PathVariable Long id, @Valid @RequestBody RoleUpdateReq request) {
        return ApiResponse.ok(roleService.update(id, request));
    }

    @DeleteMapping("/{id}")
    /**
     * 删除角色，并清理角色权限关联。
     */
    public ApiResponse<Void> delete(@PathVariable Long id) {
        roleService.delete(id);
        return ApiResponse.ok();
    }

    @PutMapping("/{id}/permissions")
    /**
     * 重置角色权限关系，用于单独维护角色拥有的权限集合。
     */
    public ApiResponse<Void> resetPermissions(@PathVariable Long id, @Valid @RequestBody AssignPermissionReq request) {
        roleService.resetPermissions(id, request.permissionIds());
        return ApiResponse.ok();
    }
}

