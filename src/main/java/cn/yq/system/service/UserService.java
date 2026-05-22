package cn.yq.system.service;

import cn.yq.common.PageResult;
import cn.yq.system.domain.vo.req.UserCreateReq;
import cn.yq.system.domain.vo.req.UserUpdateReq;
import cn.yq.system.domain.vo.res.UserDetailRes;

import java.util.List;

/**
 * 系统用户业务接口，负责用户基础信息维护和用户角色绑定。
 */
public interface UserService {
    PageResult<UserDetailRes> list(String keyword, int pageNum, int pageSize);

    UserDetailRes get(Long id);

    UserDetailRes create(UserCreateReq request);

    UserDetailRes update(Long id, UserUpdateReq request);

    void delete(Long id);

    void resetRoles(Long userId, List<Long> roleIds);
}
