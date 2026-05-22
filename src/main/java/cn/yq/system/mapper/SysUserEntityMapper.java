package cn.yq.system.mapper;

import cn.yq.system.domain.entity.SysUserEntity;
import org.apache.ibatis.annotations.Delete;
import org.apache.ibatis.annotations.Insert;
import org.apache.ibatis.annotations.Options;
import org.apache.ibatis.annotations.Param;
import org.apache.ibatis.annotations.Select;
import org.apache.ibatis.annotations.Update;

import java.util.List;

/**
 * 系统用户数据访问接口。
 */
public interface SysUserEntityMapper {
    /**
     * 查询用户列表，keyword 为空时返回全部用户。
     */
    @Select("""
            <script>
            select * from sys_user
            <where>
              <if test="keyword != null and keyword != ''">
                and (username like concat('%', #{keyword}, '%') or nickname like concat('%', #{keyword}, '%'))
              </if>
            </where>
            order by id desc
            </script>
            """)
    List<SysUserEntity> list(@Param("keyword") String keyword);

    /**
     * 根据主键查询用户。
     */
    @Select("select * from sys_user where id = #{id}")
    SysUserEntity findById(Long id);

    /**
     * 根据用户名查询用户，用于创建时校验用户名唯一性。
     */
    @Select("select * from sys_user where username = #{username}")
    SysUserEntity findByUsername(String username);

    /**
     * 新增用户，并回填自增主键。
     */
    @Insert("""
            insert into sys_user(username, nickname, password, phone, email, status)
            values(#{username}, #{nickname}, #{password}, #{phone}, #{email}, #{status})
            """)
    @Options(useGeneratedKeys = true, keyProperty = "id")
    int insert(SysUserEntity user);

    /**
     * 更新用户基础信息。
     */
    @Update("""
            update sys_user
            set nickname = #{nickname},
                password = #{password},
                phone = #{phone},
                email = #{email},
                status = #{status},
                updated_at = current_timestamp
            where id = #{id}
            """)
    int update(SysUserEntity user);

    /**
     * 根据主键删除用户。
     */
    @Delete("delete from sys_user where id = #{id}")
    int deleteById(Long id);
}

