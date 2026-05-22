package cn.yq.system.service.impl;

import cn.yq.common.PageResult;
import cn.yq.exception.BusinessException;
import cn.yq.system.domain.entity.SysPermissionEntity;
import cn.yq.system.domain.entity.SysRoleEntity;
import cn.yq.system.domain.entity.SysUserEntity;
import cn.yq.system.domain.vo.req.UserCreateReq;
import cn.yq.system.domain.vo.req.UserUpdateReq;
import cn.yq.system.domain.vo.res.PermissionDetailRes;
import cn.yq.system.domain.vo.res.RoleDetailRes;
import cn.yq.system.domain.vo.res.UserDetailRes;
import cn.yq.system.mapper.RelationMapper;
import cn.yq.system.mapper.SysPermissionEntityMapper;
import cn.yq.system.mapper.SysRoleEntityMapper;
import cn.yq.system.mapper.SysUserEntityMapper;
import cn.yq.system.service.UserService;
import com.github.pagehelper.PageHelper;
import com.github.pagehelper.PageInfo;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.StringUtils;

import java.util.Collections;
import java.util.List;

@Service
public class UserServiceImpl implements UserService {
    private final SysUserEntityMapper userMapper;
    private final SysRoleEntityMapper roleMapper;
    private final SysPermissionEntityMapper permissionMapper;
    private final RelationMapper relationMapper;

    public UserServiceImpl(
            SysUserEntityMapper userMapper,
            SysRoleEntityMapper roleMapper,
            SysPermissionEntityMapper permissionMapper,
            RelationMapper relationMapper
    ) {
        this.userMapper = userMapper;
        this.roleMapper = roleMapper;
        this.permissionMapper = permissionMapper;
        this.relationMapper = relationMapper;
    }

    @Override
    public PageResult<UserDetailRes> list(String keyword, int pageNum, int pageSize) {
        PageInfo<SysUserEntity> page = PageHelper.startPage(pageNum, pageSize)
                .doSelectPageInfo(() -> userMapper.list(keyword));
        return new PageResult<>(page.getTotal(), page.getList().stream().map(this::toResponse).toList());
    }

    @Override
    public UserDetailRes get(Long id) {
        return toResponse(requireUser(id));
    }

    @Override
    @Transactional
    public UserDetailRes create(UserCreateReq request) {
        if (userMapper.findByUsername(request.username()) != null) {
            throw new BusinessException("用户名已存在");
        }
        SysUserEntity user = new SysUserEntity();
        user.setUsername(request.username());
        user.setPassword(request.password());
        user.setNickname(request.nickname());
        user.setPhone(request.phone());
        user.setEmail(request.email());
        user.setStatus(request.status() == null ? 1 : request.status());
        userMapper.insert(user);
        resetRoles(user.getId(), request.roleIds());
        return get(user.getId());
    }

    @Override
    @Transactional
    public UserDetailRes update(Long id, UserUpdateReq request) {
        SysUserEntity user = requireUser(id);
        if (StringUtils.hasText(request.password())) {
            user.setPassword(request.password());
        }
        user.setNickname(request.nickname());
        user.setPhone(request.phone());
        user.setEmail(request.email());
        user.setStatus(request.status() == null ? 1 : request.status());
        userMapper.update(user);
        if (request.roleIds() != null) {
            resetRoles(id, request.roleIds());
        }
        return get(id);
    }

    @Override
    @Transactional
    public void delete(Long id) {
        requireUser(id);
        relationMapper.deleteUserRoles(id);
        userMapper.deleteById(id);
    }

    @Override
    @Transactional
    public void resetRoles(Long userId, List<Long> roleIds) {
        requireUser(userId);
        relationMapper.deleteUserRoles(userId);
        List<Long> ids = distinctIds(roleIds);
        ids.forEach(this::requireRole);
        if (!ids.isEmpty()) {
            relationMapper.insertUserRoles(userId, ids);
        }
    }

    private SysUserEntity requireUser(Long id) {
        SysUserEntity user = userMapper.findById(id);
        if (user == null) {
            throw new BusinessException("用户不存在");
        }
        return user;
    }

    private SysRoleEntity requireRole(Long id) {
        SysRoleEntity role = roleMapper.findById(id);
        if (role == null) {
            throw new BusinessException("角色不存在：" + id);
        }
        return role;
    }

    private UserDetailRes toResponse(SysUserEntity user) {
        return new UserDetailRes(
                user.getId(),
                user.getUsername(),
                user.getNickname(),
                user.getPhone(),
                user.getEmail(),
                user.getStatus(),
                roleMapper.findByUserId(user.getId()).stream().map(this::toRoleDetailRes).toList(),
                permissionMapper.findByUserId(user.getId()).stream().map(this::toPermissionDetailRes).toList(),
                user.getCreatedAt(),
                user.getUpdatedAt()
        );
    }

    private RoleDetailRes toRoleDetailRes(SysRoleEntity role) {
        return new RoleDetailRes(
                role.getId(),
                role.getRoleCode(),
                role.getRoleName(),
                role.getDescription(),
                role.getStatus(),
                permissionMapper.findByRoleId(role.getId()).stream().map(this::toPermissionDetailRes).toList(),
                role.getCreatedAt(),
                role.getUpdatedAt()
        );
    }

    private PermissionDetailRes toPermissionDetailRes(SysPermissionEntity permission) {
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

    private List<Long> distinctIds(List<Long> ids) {
        if (ids == null) {
            return Collections.emptyList();
        }
        return ids.stream().distinct().toList();
    }
}
