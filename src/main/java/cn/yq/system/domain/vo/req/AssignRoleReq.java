package cn.yq.system.domain.vo.req;

import jakarta.validation.constraints.NotNull;

import java.util.List;

public record AssignRoleReq(
        @NotNull List<Long> roleIds
) {
}
