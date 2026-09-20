package com.login.schoolregistersystem.vo;

import lombok.Data;

/**
 * 登录成功返回数据（契约：token + 用户基本信息）
 */
@Data
public class LoginVO {

    /** 登录令牌 */
    private String token;

    /** 用户ID */
    private Long userId;

    /** 用户名 */
    private String username;

    /** 真实姓名 */
    private String realName;
}
