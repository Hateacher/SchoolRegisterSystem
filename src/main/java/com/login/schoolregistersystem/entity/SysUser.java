package com.login.schoolregistersystem.entity;

import lombok.Data;

/**
 * 系统用户实体（对应表 sys_user）
 */
@Data
public class SysUser {

    /** 用户ID */
    private Long userId;

    /** 用户名 */
    private String username;

    /** 密码（SHA-256 摘要） */
    private String password;

    /** 真实姓名 */
    private String realName;

    /** 登录令牌 */
    private String token;
}
