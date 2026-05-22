package cn.yq.system.domain.vo.req;

import jakarta.validation.constraints.NotNull;

import java.util.List;

public record AssignPermissionReq(
        @NotNull List<Long> permissionIds
) {
}
