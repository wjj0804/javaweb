package cn.yq.system.service;

import cn.yq.common.PageResult;
import cn.yq.system.domain.vo.req.PermissionCreateReq;
import cn.yq.system.domain.vo.req.PermissionUpdateReq;
import cn.yq.system.domain.vo.res.PermissionDetailRes;

/**
 * 系统权限业务接口，负责菜单和按钮权限节点维护。
 */
public interface PermissionService {
    PageResult<PermissionDetailRes> list(String keyword, String type, int pageNum, int pageSize);

    PermissionDetailRes get(Long id);

    PermissionDetailRes create(PermissionCreateReq request);

    PermissionDetailRes update(Long id, PermissionUpdateReq request);

    void delete(Long id);
}
