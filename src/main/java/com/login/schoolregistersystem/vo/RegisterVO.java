package com.login.schoolregistersystem.vo;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import lombok.Data;

/**
 * 注册请求参数
 */
@Data
public class RegisterVO {

    /** 用户名（3~20位） */
    @NotBlank(message = "用户名不能为空")
    @Size(min = 3, max = 20, message = "用户名长度须为3~20位")
    private String username;

    /** 密码（6~20位） */
    @NotBlank(message = "密码不能为空")
    @Size(min = 6, max = 20, message = "密码长度须为6~20位")
    private String password;

    /** 确认密码 */
    @NotBlank(message = "确认密码不能为空")
    private String confirmPassword;

    /** 真实姓名 */
    @NotBlank(message = "真实姓名不能为空")
    @Size(max = 10, message = "真实姓名不能超过10个字")
    private String realName;
}
