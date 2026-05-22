package cn.yq.system.domain.vo.req;

import java.util.List;

public record UserUpdateReq(
        String password,
        String nickname,
        String phone,
        String email,
        Integer status,
        List<Long> roleIds
) {
}


