package com.login.schoolregistersystem.mapper;

import com.login.schoolregistersystem.entity.SysUser;
import org.apache.ibatis.annotations.Insert;
import org.apache.ibatis.annotations.Options;
import org.apache.ibatis.annotations.Param;
import org.apache.ibatis.annotations.Select;
import org.apache.ibatis.annotations.Update;

public interface UserMapper {

    String COLUMNS = "user_id AS userId, username, password, real_name AS realName, token";

    /** 按用户名查询（注册查重、登录校验） */
    @Select("SELECT " + COLUMNS + " FROM sys_user WHERE username = #{username}")
    SysUser selectByUsername(@Param("username") String username);

    /** 按令牌查询（登录拦截器校验 token） */
    @Select("SELECT " + COLUMNS + " FROM sys_user WHERE token = #{token}")
    SysUser selectByToken(@Param("token") String token);

    /** 新增用户，主键回填到 userId */
    @Insert("INSERT INTO sys_user(username, password, real_name) "
            + "VALUES(#{username}, #{password}, #{realName})")
    @Options(useGeneratedKeys = true, keyProperty = "userId", keyColumn = "user_id")
    int insert(SysUser user);

    /** 登录成功后写入令牌 */
    @Update("UPDATE sys_user SET token = #{token} WHERE user_id = #{userId}")
    int updateToken(@Param("userId") Long userId, @Param("token") String token);
}
