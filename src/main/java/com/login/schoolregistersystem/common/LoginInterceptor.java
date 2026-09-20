package com.login.schoolregistersystem.common;

import com.login.schoolregistersystem.entity.SysUser;
import com.login.schoolregistersystem.service.UserService;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;
import org.springframework.web.servlet.HandlerInterceptor;

/**
 * 登录拦截器：校验请求头 token（契约约定：请求头名 token，不使用 Bearer）
 * 校验通过后把当前登录用户ID放入 request 属性 loginUserId
 */
@Component
@RequiredArgsConstructor
public class LoginInterceptor implements HandlerInterceptor {

    public static final String TOKEN_HEADER = "token";
    public static final String LOGIN_USER_ATTRIBUTE = "loginUserId";

    private final UserService userService;

    @Override
    public boolean preHandle(HttpServletRequest request, HttpServletResponse response, Object handler)
            throws Exception {
        String token = request.getHeader(TOKEN_HEADER);
        SysUser user = (token == null || token.isBlank()) ? null : userService.getByToken(token);
        if (user == null) {
            response.setContentType("application/json;charset=UTF-8");
            response.getWriter().write("{\"code\":401,\"msg\":\"未登录或登录已过期，请重新登录\",\"data\":null}");
            return false;
        }
        request.setAttribute(LOGIN_USER_ATTRIBUTE, user.getUserId());
        return true;
    }
}
