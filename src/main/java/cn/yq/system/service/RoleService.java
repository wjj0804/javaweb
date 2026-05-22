package cn.yq.system.service;

import cn.yq.common.PageResult;
import cn.yq.system.domain.vo.req.RoleCreateReq;
import cn.yq.system.domain.vo.req.RoleUpdateReq;
import cn.yq.system.domain.vo.res.RoleDetailRes;

import java.util.List;

/**
 * 系统角色业务接口，负责角色基础信息维护和角色权限绑定。
 */
public interface RoleService {
    PageResult<RoleDetailRes> list(String keyword, int pageNum, int pageSize);

    RoleDetailRes get(Long id);

    RoleDetailRes create(RoleCreateReq request);

    RoleDetailRes update(Long id, RoleUpdateReq request);

    void delete(Long id);

    void resetPermissions(Long roleId, List<Long> permissionIds);
}
