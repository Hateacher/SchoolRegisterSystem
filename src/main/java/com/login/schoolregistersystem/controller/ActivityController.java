package com.login.schoolregistersystem.controller;

import com.login.schoolregistersystem.common.LoginInterceptor;
import com.login.schoolregistersystem.common.Result;
import com.login.schoolregistersystem.entity.Activity;
import com.login.schoolregistersystem.service.ActivityService;
import jakarta.servlet.http.HttpServletRequest;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

/**
 * 活动模块接口（契约：/api/activity）
 * 创建者ID一律取自登录态（token），不信任请求体中的 createUserId
 */
@RestController
@RequestMapping("/api/activity")
@RequiredArgsConstructor
public class ActivityController {

    private final ActivityService activityService;

    /** 新增活动 */
    @PostMapping("/add")
    public Result<Long> add(@RequestBody Activity activity, HttpServletRequest request) {
        Long loginUserId = getLoginUserId(request);
        return Result.success("发布成功", activityService.add(activity, loginUserId));
    }

    /** 编辑活动 */
    @PutMapping("/update")
    public Result<Void> update(@RequestBody Activity activity, HttpServletRequest request) {
        activityService.update(activity, getLoginUserId(request));
        return Result.success("修改成功", null);
    }

    /** 删除活动（下架） */
    @DeleteMapping("/delete/{id}")
    public Result<Void> delete(@PathVariable Long id, HttpServletRequest request) {
        activityService.delete(id, getLoginUserId(request));
        return Result.success("删除成功", null);
    }

    /** 活动列表 */
    @GetMapping("/list")
    public Result<List<Activity>> list() {
        return Result.success(activityService.list());
    }

    /** 活动详情 */
    @GetMapping("/detail/{id}")
    public Result<Activity> detail(@PathVariable Long id) {
        return Result.success(activityService.detail(id));
    }

    private static Long getLoginUserId(HttpServletRequest request) {
        return (Long) request.getAttribute(LoginInterceptor.LOGIN_USER_ATTRIBUTE);
    }
}
