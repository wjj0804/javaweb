package cn.yq.system.mapper;

import cn.yq.system.domain.entity.SysPermissionEntity;
import org.apache.ibatis.annotations.Delete;
import org.apache.ibatis.annotations.Insert;
import org.apache.ibatis.annotations.Options;
import org.apache.ibatis.annotations.Param;
import org.apache.ibatis.annotations.Select;
import org.apache.ibatis.annotations.Update;

import java.util.List;

/**
 * 系统权限数据访问接口。
 */
public interface SysPermissionEntityMapper {
    /**
     * 查询权限列表，支持关键字和权限类型过滤。
     */
    @Select("""
            <script>
            select * from sys_permission
            <where>
              <if test="keyword != null and keyword != ''">
                and (permission_code like concat('%', #{keyword}, '%') or permission_name like concat('%', #{keyword}, '%'))
              </if>
              <if test="type != null and type != ''">
                and permission_type = #{type}
              </if>
            </where>
            order by sort_order asc, id asc
            </script>
            """)
    List<SysPermissionEntity> list(@Param("keyword") String keyword, @Param("type") String type);

    /**
     * 根据主键查询权限。
     */
    @Select("select * from sys_permission where id = #{id}")
    SysPermissionEntity findById(Long id);

    /**
     * 根据权限编码查询权限，用于创建时校验编码唯一性。
     */
    @Select("select * from sys_permission where permission_code = #{permissionCode}")
    SysPermissionEntity findByPermissionCode(String permissionCode);

    /**
     * 查询角色已绑定的权限集合。
     */
    @Select("""
            select p.* from sys_permission p
            inner join sys_role_permission rp on rp.permission_id = p.id
            where rp.role_id = #{roleId}
            order by p.sort_order asc, p.id asc
            """)
    List<SysPermissionEntity> findByRoleId(Long roleId);

    /**
     * 查询用户最终拥有的权限集合，权限来源于用户绑定的角色。
     */
    @Select("""
            select distinct p.* from sys_permission p
            inner join sys_role_permission rp on rp.permission_id = p.id
            inner join sys_user_role ur on ur.role_id = rp.role_id
            where ur.user_id = #{userId}
            order by p.sort_order asc, p.id asc
            """)
    List<SysPermissionEntity> findByUserId(Long userId);

    /**
     * 新增权限节点，并回填自增主键。
     */
    @Insert("""
            insert into sys_permission(parent_id, permission_code, permission_name, permission_type, path, component, description, sort_order, status)
            values(#{parentId}, #{permissionCode}, #{permissionName}, #{permissionType}, #{path}, #{component}, #{description}, #{sortOrder}, #{status})
            """)
    @Options(useGeneratedKeys = true, keyProperty = "id")
    int insert(SysPermissionEntity permission);

    /**
     * 更新权限节点信息。
     */
    @Update("""
            update sys_permission
            set parent_id = #{parentId},
                permission_name = #{permissionName},
                permission_type = #{permissionType},
                path = #{path},
                component = #{component},
                description = #{description},
                sort_order = #{sortOrder},
                status = #{status},
                updated_at = current_timestamp
            where id = #{id}
            """)
    int update(SysPermissionEntity permission);

    /**
     * 根据主键删除权限。
     */
    @Delete("delete from sys_permission where id = #{id}")
    int deleteById(Long id);
}

