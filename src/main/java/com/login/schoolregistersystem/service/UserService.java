package com.login.schoolregistersystem.service;

import com.login.schoolregistersystem.entity.SysUser;
import com.login.schoolregistersystem.vo.LoginRequestVO;
import com.login.schoolregistersystem.vo.LoginVO;
import com.login.schoolregistersystem.vo.RegisterVO;

public interface UserService {

    /** 注册：校验用户名重复、两次密码一致，返回新用户ID */
    Long register(RegisterVO vo);

    /** 登录：校验用户与密码，生成 token 并入库，返回 token 和用户信息 */
    LoginVO login(LoginRequestVO vo);

    /** 按 token 查询用户（登录拦截器使用），不存在返回 null */
    SysUser getByToken(String token);
}
