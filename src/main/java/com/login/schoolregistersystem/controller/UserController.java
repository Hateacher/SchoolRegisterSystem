package com.login.schoolregistersystem.controller;

import com.login.schoolregistersystem.common.Result;
import com.login.schoolregistersystem.service.UserService;
import com.login.schoolregistersystem.vo.LoginRequestVO;
import com.login.schoolregistersystem.vo.LoginVO;
import com.login.schoolregistersystem.vo.RegisterVO;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

/**
 * 用户模块接口（契约：/api/user）
 */
@RestController
@RequestMapping("/api/user")
@RequiredArgsConstructor
public class UserController {

    private final UserService userService;

    /** 用户登录 */
    @PostMapping("/login")
    public Result<LoginVO> login(@RequestBody @Valid LoginRequestVO vo) {
        return Result.success("登录成功", userService.login(vo));
    }

    /** 用户注册 */
    @PostMapping("/register")
    public Result<Long> register(@RequestBody @Valid RegisterVO vo) {
        return Result.success("注册成功", userService.register(vo));
    }
}
