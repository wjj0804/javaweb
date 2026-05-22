package cn.yq.system.service.impl;

import cn.yq.common.PageResult;
import cn.yq.exception.BusinessException;
import cn.yq.system.domain.entity.SysPermissionEntity;
import cn.yq.system.domain.entity.SysRoleEntity;
import cn.yq.system.domain.vo.req.RoleCreateReq;
import cn.yq.system.domain.vo.req.RoleUpdateReq;
import cn.yq.system.domain.vo.res.PermissionDetailRes;
import cn.yq.system.domain.vo.res.RoleDetailRes;
import cn.yq.system.mapper.RelationMapper;
import cn.yq.system.mapper.SysPermissionEntityMapper;
import cn.yq.system.mapper.SysRoleEntityMapper;
import cn.yq.system.service.RoleService;
import com.github.pagehelper.PageHelper;
import com.github.pagehelper.PageInfo;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Collections;
import java.util.List;

@Service
public class RoleServiceImpl implements RoleService {
    private final SysRoleEntityMapper roleMapper;
    private final SysPermissionEntityMapper permissionMapper;
    private final RelationMapper relationMapper;

    public RoleServiceImpl(
            SysRoleEntityMapper roleMapper,
            SysPermissionEntityMapper permissionMapper,
            RelationMapper relationMapper
    ) {
        this.roleMapper = roleMapper;
        this.permissionMapper = permissionMapper;
        this.relationMapper = relationMapper;
    }

    @Override
    public PageResult<RoleDetailRes> list(String keyword, int pageNum, int pageSize) {
        PageInfo<SysRoleEntity> page = PageHelper.startPage(pageNum, pageSize)
                .doSelectPageInfo(() -> roleMapper.list(keyword));
        return new PageResult<>(page.getTotal(), page.getList().stream().map(this::toResponse).toList());
    }

    @Override
    public RoleDetailRes get(Long id) {
        return toResponse(requireRole(id));
    }

    @Override
    @Transactional
    public RoleDetailRes create(RoleCreateReq request) {
        if (roleMapper.findByRoleCode(request.roleCode()) != null) {
            throw new BusinessException("角色编码已存在");
        }
        SysRoleEntity role = new SysRoleEntity();
        role.setRoleCode(request.roleCode());
        role.setRoleName(request.roleName());
        role.setDescription(request.description());
        role.setStatus(defaultStatus(request.status()));
        roleMapper.insert(role);
        resetPermissions(role.getId(), request.permissionIds());
        return get(role.getId());
    }

    @Override
    @Transactional
    public RoleDetailRes update(Long id, RoleUpdateReq request) {
        SysRoleEntity role = requireRole(id);
        role.setRoleName(request.roleName());
        role.setDescription(request.description());
        role.setStatus(defaultStatus(request.status()));
        roleMapper.update(role);
        if (request.permissionIds() != null) {
            resetPermissions(id, request.permissionIds());
        }
        return get(id);
    }

    @Override
    @Transactional
    public void delete(Long id) {
        requireRole(id);
        relationMapper.deleteRolePermissions(id);
        roleMapper.deleteById(id);
    }

    @Override
    @Transactional
    public void resetPermissions(Long roleId, List<Long> permissionIds) {
        requireRole(roleId);
        relationMapper.deleteRolePermissions(roleId);
        List<Long> ids = distinctIds(permissionIds);
        ids.forEach(this::requirePermission);
        if (!ids.isEmpty()) {
            relationMapper.insertRolePermissions(roleId, ids);
        }
    }

    private SysRoleEntity requireRole(Long id) {
        SysRoleEntity role = roleMapper.findById(id);
        if (role == null) {
            throw new BusinessException("角色不存在");
        }
        return role;
    }

    private SysPermissionEntity requirePermission(Long id) {
        SysPermissionEntity permission = permissionMapper.findById(id);
        if (permission == null) {
            throw new BusinessException("权限不存在：" + id);
        }
        return permission;
    }

    private RoleDetailRes toResponse(SysRoleEntity role) {
        return new RoleDetailRes(
                role.getId(),
                role.getRoleCode(),
                role.getRoleName(),
                role.getDescription(),
                role.getStatus(),
                permissionMapper.findByRoleId(role.getId()).stream().map(this::toPermissionResponse).toList(),
                role.getCreatedAt(),
                role.getUpdatedAt()
        );
    }

    private PermissionDetailRes toPermissionResponse(SysPermissionEntity permission) {
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

    private int defaultStatus(Integer status) {
        return status == null ? 1 : status;
    }

    private List<Long> distinctIds(List<Long> ids) {
        if (ids == null) {
            return Collections.emptyList();
        }
        return ids.stream().distinct().toList();
    }
}
