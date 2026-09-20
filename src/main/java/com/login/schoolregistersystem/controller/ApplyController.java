package com.login.schoolregistersystem.controller;

import com.login.schoolregistersystem.common.LoginInterceptor;
import com.login.schoolregistersystem.common.Result;
import com.login.schoolregistersystem.service.ApplyService;
import com.login.schoolregistersystem.vo.ApplyVO;
import jakarta.servlet.http.HttpServletRequest;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

/**
 * 报名模块接口（契约：/api/apply）
 */
@RestController
@RequestMapping("/api/apply")
@RequiredArgsConstructor
public class ApplyController {

    private final ApplyService applyService;

    /** 活动报名 */
    @PostMapping("/{activityId}")
    public Result<Void> apply(@PathVariable Long activityId, HttpServletRequest request) {
        applyService.apply(activityId, getLoginUserId(request));
        return Result.success("报名成功", null);
    }

    /** 取消报名 */
    @DeleteMapping("/{activityId}")
    public Result<Void> cancel(@PathVariable Long activityId, HttpServletRequest request) {
        applyService.cancel(activityId, getLoginUserId(request));
        return Result.success("取消报名成功", null);
    }

    /** 我的报名 */
    @GetMapping("/my")
    public Result<List<ApplyVO>> my(HttpServletRequest request) {
        return Result.success(applyService.myApplies(getLoginUserId(request)));
    }

    private static Long getLoginUserId(HttpServletRequest request) {
        return (Long) request.getAttribute(LoginInterceptor.LOGIN_USER_ATTRIBUTE);
    }
}
