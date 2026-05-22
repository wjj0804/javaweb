package cn.yq.system.mapper;

import cn.yq.system.domain.entity.SysRolePermissionEntity;
import org.apache.ibatis.annotations.Delete;
import org.apache.ibatis.annotations.Insert;
import org.apache.ibatis.annotations.Param;
import org.apache.ibatis.annotations.Select;

import java.util.List;

/**
 * 关系表数据访问接口，集中维护用户角色和角色权限两类绑定关系。
 */
public interface RelationMapper {
    /**
     * 删除用户的全部角色绑定。
     */
    @Delete("delete from sys_user_role where user_id = #{userId}")
    int deleteUserRoles(Long userId);

    /**
     * 批量新增用户角色绑定。
     */
    @Insert("""
            <script>
            insert into sys_user_role(user_id, role_id) values
            <foreach collection="roleIds" item="roleId" separator=",">
              (#{userId}, #{roleId})
            </foreach>
            </script>
            """)
    int insertUserRoles(@Param("userId") Long userId, @Param("roleIds") List<Long> roleIds);

    /**
     * 删除角色的全部权限绑定。
     */
    @Delete("delete from sys_role_permission where role_id = #{roleId}")
    int deleteRolePermissions(Long roleId);

    /**
     * 批量新增角色权限绑定。
     */
    @Insert("""
            <script>
            insert into sys_role_permission(role_id, permission_id) values
            <foreach collection="permissionIds" item="permissionId" separator=",">
              (#{roleId}, #{permissionId})
            </foreach>
            </script>
            """)
    int insertRolePermissions(@Param("roleId") Long roleId, @Param("permissionIds") List<Long> permissionIds);

    /**
     * 查询用户已绑定的角色 ID 集合。
     */
    @Select("select role_id from sys_user_role where user_id = #{userId}")
    List<Long> findRoleIdsByUserId(Long userId);

    /**
     * 查询角色已绑定的权限 ID 集合。
     */
    @Select("select permission_id from sys_role_permission where role_id = #{roleId}")
    List<Long> findPermissionIdsByRoleId(Long roleId);

    /**
     * 查询角色权限关联记录，用于需要关联表明细的场景。
     */
    @Select("select * from sys_role_permission where role_id = #{roleId} order by id")
    List<SysRolePermissionEntity> findRolePermissionsByRoleId(Long roleId);
}

