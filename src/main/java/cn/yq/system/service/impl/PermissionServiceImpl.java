package cn.yq.system.service.impl;

import cn.yq.common.PageResult;
import cn.yq.exception.BusinessException;
import cn.yq.system.domain.entity.SysPermissionEntity;
import cn.yq.system.domain.vo.req.PermissionCreateReq;
import cn.yq.system.domain.vo.req.PermissionUpdateReq;
import cn.yq.system.domain.vo.res.PermissionDetailRes;
import cn.yq.system.mapper.SysPermissionEntityMapper;
import cn.yq.system.service.PermissionService;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

@Service
public class PermissionServiceImpl implements PermissionService {
    private final SysPermissionEntityMapper permissionMapper;

    public PermissionServiceImpl(SysPermissionEntityMapper permissionMapper) {
        this.permissionMapper = permissionMapper;
    }

    @Override
    public PageResult<PermissionDetailRes> list(String keyword, String type, int pageNum, int pageSize) {
        List<SysPermissionEntity> permissions = permissionMapper.list(keyword, type);
        return new PageResult<>((long) permissions.size(), buildTree(permissions));
    }

    @Override
    public PermissionDetailRes get(Long id) {
        return toResponse(requirePermission(id));
    }

    @Override
    public PermissionDetailRes create(PermissionCreateReq request) {
        if (permissionMapper.findByPermissionCode(request.permissionCode()) != null) {
            throw new BusinessException("权限编码已存在");
        }
        SysPermissionEntity permission = new SysPermissionEntity();
        permission.setParentId(request.parentId() == null ? 0L : request.parentId());
        permission.setPermissionCode(request.permissionCode());
        permission.setPermissionName(request.permissionName());
        permission.setPermissionType(request.permissionType());
        permission.setPath(request.path());
        permission.setComponent(request.component());
        permission.setDescription(request.description());
        permission.setSortOrder(request.sortOrder() == null ? 0 : request.sortOrder());
        permission.setStatus(request.status() == null ? 1 : request.status());
        permissionMapper.insert(permission);
        return get(permission.getId());
    }

    @Override
    public PermissionDetailRes update(Long id, PermissionUpdateReq request) {
        SysPermissionEntity permission = requirePermission(id);
        permission.setParentId(request.parentId() == null ? 0L : request.parentId());
        permission.setPermissionName(request.permissionName());
        permission.setPermissionType(request.permissionType());
        permission.setPath(request.path());
        permission.setComponent(request.component());
        permission.setDescription(request.description());
        permission.setSortOrder(request.sortOrder() == null ? 0 : request.sortOrder());
        permission.setStatus(request.status() == null ? 1 : request.status());
        permissionMapper.update(permission);
        return get(id);
    }

    @Override
    public void delete(Long id) {
        requirePermission(id);
        permissionMapper.deleteById(id);
    }

    private SysPermissionEntity requirePermission(Long id) {
        SysPermissionEntity permission = permissionMapper.findById(id);
        if (permission == null) {
            throw new BusinessException("权限不存在");
        }
        return permission;
    }

    private PermissionDetailRes toResponse(SysPermissionEntity permission) {
        return new PermissionDetailRes(
                permission.getId(),
                permission.getParentId(),
                permission.getPermissionCode(),
                permission.getPermissionName(),
                permission.getPermissionType(),
                permission.getPath(),
                permission.getComponent(),
                permission.getDescription(),
                permission.getSortOrder(),
                permission.getStatus(),
                permission.getCreatedAt(),
                permission.getUpdatedAt(),
                null
        );
    }

    private List<PermissionDetailRes> buildTree(List<SysPermissionEntity> permissions) {
        Map<Long, List<SysPermissionEntity>> childrenMap = new LinkedHashMap<>();
        permissions.forEach(permission -> childrenMap
                .computeIfAbsent(permission.getParentId(), key -> new ArrayList<>())
                .add(permission));

        List<PermissionDetailRes> roots = new ArrayList<>();
        permissions.stream()
                .filter(permission -> permission.getParentId() == null
                        || permission.getParentId() == 0
                        || !containsPermission(permissions, permission.getParentId()))
                .forEach(permission -> roots.add(toTreeNode(permission, childrenMap)));
        return roots;
    }

    private PermissionDetailRes toTreeNode(
            SysPermissionEntity permission,
            Map<Long, List<SysPermissionEntity>> childrenMap
    ) {
        List<PermissionDetailRes> children = childrenMap.getOrDefault(permission.getId(), List.of())
                .stream()
                .map(child -> toTreeNode(child, childrenMap))
                .toList();
        return new PermissionDetailRes(
                permission.getId(),
                permission.getParentId(),
                permission.getPermissionCode(),
                permission.getPermissionName(),
                permission.getPermissionType(),
                permission.getPath(),
                permission.getComponent(),
                permission.getDescription(),
                permission.getSortOrder(),
                permission.getStatus(),
                permission.getCreatedAt(),
                permission.getUpdatedAt(),
                children.isEmpty() ? null : children
        );
    }

    private boolean containsPermission(List<SysPermissionEntity> permissions, Long id) {
        return permissions.stream().anyMatch(permission -> permission.getId().equals(id));
    }
}
