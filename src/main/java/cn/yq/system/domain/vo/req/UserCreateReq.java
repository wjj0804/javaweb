package cn.yq.system.domain.vo.req;

import jakarta.validation.constraints.NotBlank;

import java.util.List;

public record UserCreateReq(
        @NotBlank String username,
        @NotBlank String password,
        String nickname,
        String phone,
        String email,
        Integer status,
        List<Long> roleIds
) {
}


