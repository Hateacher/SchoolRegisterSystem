package com.login.schoolregistersystem.common;

import com.login.schoolregistersystem.entity.SysUser;
import com.login.schoolregistersystem.service.UserService;
import com.login.schoolregistersystem.service.impl.UserServiceImpl;
import com.login.schoolregistersystem.vo.RegisterVO;
import org.junit.jupiter.api.Test;
import org.springframework.http.HttpMethod;
import org.springframework.mock.web.MockHttpServletRequest;
import org.springframework.mock.web.MockHttpServletResponse;
import org.springframework.validation.BeanPropertyBindingResult;
import org.springframework.validation.FieldError;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.servlet.resource.NoResourceFoundException;

import java.lang.reflect.Method;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

class CommonUtilitiesTest {

    private final GlobalExceptionHandler exceptionHandler = new GlobalExceptionHandler();

    @Test
    void resultSuccessShouldReturnStandardSuccessPayload() {
        Result<String> result = Result.success("ok");

        assertEquals(200, result.getCode());
        assertEquals("成功", result.getMsg());
        assertEquals("ok", result.getData());
    }

    @Test
    void resultErrorShouldReturnProvidedCodeAndMessage() {
        Result<Void> result = Result.error(400, "业务错误");

        assertEquals(400, result.getCode());
        assertEquals("业务错误", result.getMsg());
        assertNull(result.getData());
    }

    @Test
    void businessExceptionShouldExposeCodeAndMessage() {
        BusinessException ex = new BusinessException(401, "未登录");

        assertEquals(401, ex.getCode());
        assertEquals("未登录", ex.getMessage());
    }

    @Test
    void globalExceptionHandlerShouldConvertBusinessExceptions() {
        BusinessException ex = new BusinessException("用户名已存在");

        Result<Void> result = exceptionHandler.handleBusinessException(ex);

        assertEquals(400, result.getCode());
        assertEquals("用户名已存在", result.getMsg());
    }

    @Test
    void globalExceptionHandlerShouldUseFieldValidationMessage() throws NoSuchMethodException {
        Method method = UserServiceImpl.class.getDeclaredMethod("register", RegisterVO.class);
        BeanPropertyBindingResult bindingResult = new BeanPropertyBindingResult(new RegisterVO(), "registerVO");
        bindingResult.addError(new FieldError("registerVO", "username", "用户名不能为空"));
        MethodArgumentNotValidException ex = new MethodArgumentNotValidException(new org.springframework.core.MethodParameter(method, 0), bindingResult);

        Result<Void> result = exceptionHandler.handleValidException(ex);

        assertEquals(400, result.getCode());
        assertEquals("用户名不能为空", result.getMsg());
    }

    @Test
    void globalExceptionHandlerShouldHandleMissingResource() {
        NoResourceFoundException ex = new NoResourceFoundException(HttpMethod.GET, "/missing", "Not Found");

        Result<Void> result = exceptionHandler.handleNoResourceFoundException(ex);

        assertEquals(404, result.getCode());
        assertEquals("请求的资源不存在", result.getMsg());
    }

    @Test
    void globalExceptionHandlerShouldConvertUnknownExceptions() {
        Result<Void> result = exceptionHandler.handleException(new IllegalStateException("boom"));

        assertEquals(500, result.getCode());
        assertEquals("服务器异常", result.getMsg());
    }

    @Test
    void interceptorShouldAllowRequestWithValidToken() throws Exception {
        UserService userService = mock(UserService.class);
        LoginInterceptor interceptor = new LoginInterceptor(userService);
        MockHttpServletRequest request = new MockHttpServletRequest();
        request.addHeader(LoginInterceptor.TOKEN_HEADER, "valid-token");
        MockHttpServletResponse response = new MockHttpServletResponse();
        SysUser user = new SysUser();
        user.setUserId(77L);
        when(userService.getByToken("valid-token")).thenReturn(user);

        boolean allowed = interceptor.preHandle(request, response, new Object());

        assertTrue(allowed);
        assertEquals(77L, request.getAttribute(LoginInterceptor.LOGIN_USER_ATTRIBUTE));
    }

    @Test
    void interceptorShouldRejectRequestWithMissingOrExpiredToken() throws Exception {
        UserService userService = mock(UserService.class);
        LoginInterceptor interceptor = new LoginInterceptor(userService);
        MockHttpServletRequest request = new MockHttpServletRequest();
        MockHttpServletResponse response = new MockHttpServletResponse();
        when(userService.getByToken(anyString())).thenReturn(null);

        boolean allowed = interceptor.preHandle(request, response, new Object());

        assertFalse(allowed);
        assertEquals("application/json;charset=UTF-8", response.getContentType());
        assertTrue(response.getContentAsString().contains("未登录或登录已过期，请重新登录"));
    }
}
