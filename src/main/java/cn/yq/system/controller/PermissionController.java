package cn.yq.system.controller;

import cn.yq.common.ApiResponse;
import cn.yq.common.PageResult;
import cn.yq.system.domain.vo.req.PermissionCreateReq;
import cn.yq.system.domain.vo.res.PermissionDetailRes;
import cn.yq.system.domain.vo.req.PermissionQueryReq;
import cn.yq.system.domain.vo.req.PermissionUpdateReq;
import cn.yq.system.service.PermissionService;
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
@RequestMapping("/api/system/permissions")
/**
 * 系统权限接口，提供菜单和按钮权限节点的维护能力。
 */
public class PermissionController {
    private final PermissionService permissionService;

    public PermissionController(PermissionService permissionService) {
        this.permissionService = permissionService;
    }

    @GetMapping
    /**
     * 分页查询权限列表，支持按关键字和权限类型过滤。
     */
    public ApiResponse<PageResult<PermissionDetailRes>> list(PermissionQueryReq request) {
        return ApiResponse.ok(permissionService.list(
                request.getKeyword(),
                request.getType(),
                request.getPageNum(),
                request.getPageSize()
        ));
    }

    @GetMapping("/{id}")
    /**
     * 根据权限 ID 查询权限详情。
     */
    public ApiResponse<PermissionDetailRes> get(@PathVariable Long id) {
        return ApiResponse.ok(permissionService.get(id));
    }

    @PostMapping
    /**
     * 创建菜单或按钮权限节点。
     */
    public ApiResponse<PermissionDetailRes> create(@Valid @RequestBody PermissionCreateReq request) {
        return ApiResponse.ok(permissionService.create(request));
    }

    @PutMapping("/{id}")
    /**
     * 更新权限节点信息。
     */
    public ApiResponse<PermissionDetailRes> update(@PathVariable Long id, @Valid @RequestBody PermissionUpdateReq request) {
        return ApiResponse.ok(permissionService.update(id, request));
    }

    @DeleteMapping("/{id}")
    /**
     * 删除权限节点。
     */
    public ApiResponse<Void> delete(@PathVariable Long id) {
        permissionService.delete(id);
        return ApiResponse.ok();
    }
}

