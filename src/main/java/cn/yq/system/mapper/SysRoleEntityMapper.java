package cn.yq.system.mapper;

import cn.yq.system.domain.entity.SysRoleEntity;
import org.apache.ibatis.annotations.Delete;
import org.apache.ibatis.annotations.Insert;
import org.apache.ibatis.annotations.Options;
import org.apache.ibatis.annotations.Param;
import org.apache.ibatis.annotations.Select;
import org.apache.ibatis.annotations.Update;

import java.util.List;

/**
 * 系统角色数据访问接口。
 */
public interface SysRoleEntityMapper {
    /**
     * 查询角色列表，keyword 为空时返回全部角色。
     */
    @Select("""
            <script>
            select * from sys_role
            <where>
              <if test="keyword != null and keyword != ''">
                and (role_code like concat('%', #{keyword}, '%') or role_name like concat('%', #{keyword}, '%'))
              </if>
            </where>
            order by id desc
            </script>
            """)
    List<SysRoleEntity> list(@Param("keyword") String keyword);

    /**
     * 根据主键查询角色。
     */
    @Select("select * from sys_role where id = #{id}")
    SysRoleEntity findById(Long id);

    /**
     * 根据角色编码查询角色，用于创建时校验编码唯一性。
     */
    @Select("select * from sys_role where role_code = #{roleCode}")
    SysRoleEntity findByRoleCode(String roleCode);

    /**
     * 查询用户已绑定的角色集合。
     */
    @Select("""
            select r.* from sys_role r
            inner join sys_user_role ur on ur.role_id = r.id
            where ur.user_id = #{userId}
            order by r.id
            """)
    List<SysRoleEntity> findByUserId(Long userId);

    /**
     * 新增角色，并回填自增主键。
     */
    @Insert("""
            insert into sys_role(role_code, role_name, description, status)
            values(#{roleCode}, #{roleName}, #{description}, #{status})
            """)
    @Options(useGeneratedKeys = true, keyProperty = "id")
    int insert(SysRoleEntity role);

    /**
     * 更新角色基础信息。
     */
    @Update("""
            update sys_role
            set role_name = #{roleName},
                description = #{description},
                status = #{status},
                updated_at = current_timestamp
            where id = #{id}
            """)
    int update(SysRoleEntity role);

    /**
     * 根据主键删除角色。
     */
    @Delete("delete from sys_role where id = #{id}")
    int deleteById(Long id);
}

