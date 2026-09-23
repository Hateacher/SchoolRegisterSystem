package com.login.schoolregistersystem;

import com.login.schoolregistersystem.common.BusinessException;
import com.login.schoolregistersystem.entity.SysUser;
import com.login.schoolregistersystem.mapper.UserMapper;
import com.login.schoolregistersystem.service.impl.UserServiceImpl;
import com.login.schoolregistersystem.vo.LoginRequestVO;
import com.login.schoolregistersystem.vo.LoginVO;
import com.login.schoolregistersystem.vo.RegisterVO;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;

import java.nio.charset.StandardCharsets;
import java.security.MessageDigest;
import java.util.HexFormat;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class UserServiceImplTest {

    private static final BCryptPasswordEncoder PASSWORD_ENCODER = new BCryptPasswordEncoder();

    @Mock
    private UserMapper userMapper;

    @InjectMocks
    private UserServiceImpl userService;

    @Test
    void registerShouldRejectDuplicateUsername() {
        RegisterVO vo = buildRegisterVo("alice", "123456", "123456", "Alice");
        when(userMapper.selectByUsername("alice")).thenReturn(new SysUser());

        BusinessException ex = assertThrows(BusinessException.class, () -> userService.register(vo));

        assertEquals("用户名已存在", ex.getMessage());
        verify(userMapper, never()).insert(any());
    }

    @Test
    void registerShouldRejectPasswordMismatch() {
        RegisterVO vo = buildRegisterVo("alice", "123456", "654321", "Alice");
        when(userMapper.selectByUsername("alice")).thenReturn(null);

        BusinessException ex = assertThrows(BusinessException.class, () -> userService.register(vo));

        assertEquals("两次输入的密码不一致", ex.getMessage());
        verify(userMapper, never()).insert(any());
    }

    @Test
    void registerShouldInsertUserAndReturnGeneratedId() {
        RegisterVO vo = buildRegisterVo("alice", "123456", "123456", "Alice");
        when(userMapper.selectByUsername("alice")).thenReturn(null);
        doAnswer(invocation -> {
            SysUser user = invocation.getArgument(0);
            user.setUserId(42L);
            return null;
        }).when(userMapper).insert(any(SysUser.class));

        Long userId = userService.register(vo);

        assertEquals(42L, userId);
        ArgumentCaptor<SysUser> captor = ArgumentCaptor.forClass(SysUser.class);
        verify(userMapper).insert(captor.capture());
        SysUser savedUser = captor.getValue();
        assertEquals("alice", savedUser.getUsername());
        assertEquals("Alice", savedUser.getRealName());
        assertNotEquals("123456", savedUser.getPassword());
        assertTrue(savedUser.getPassword().startsWith("$2"));
    }

    @Test
    void loginShouldRejectUnknownUser() {
        LoginRequestVO vo = new LoginRequestVO();
        vo.setUsername("missing");
        vo.setPassword("123456");
        when(userMapper.selectByUsername("missing")).thenReturn(null);

        BusinessException ex = assertThrows(BusinessException.class, () -> userService.login(vo));

        assertEquals("用户不存在", ex.getMessage());
    }

    @Test
    void loginShouldRejectWrongPassword() {
        SysUser user = new SysUser();
        user.setUserId(7L);
        user.setUsername("alice");
        user.setRealName("Alice");
        user.setPassword(PASSWORD_ENCODER.encode("correct-password"));

        LoginRequestVO vo = new LoginRequestVO();
        vo.setUsername("alice");
        vo.setPassword("wrong-password");
        when(userMapper.selectByUsername("alice")).thenReturn(user);

        BusinessException ex = assertThrows(BusinessException.class, () -> userService.login(vo));

        assertEquals("密码错误", ex.getMessage());
    }

    @Test
    void loginShouldCreateTokenAndReturnLoginInfo() {
        SysUser user = new SysUser();
        user.setUserId(9L);
        user.setUsername("alice");
        user.setRealName("Alice");
        user.setPassword(PASSWORD_ENCODER.encode("123456"));

        LoginRequestVO vo = new LoginRequestVO();
        vo.setUsername("alice");
        vo.setPassword("123456");
        when(userMapper.selectByUsername("alice")).thenReturn(user);

        LoginVO loginVO = userService.login(vo);

        assertNotNull(loginVO);
        assertEquals(9L, loginVO.getUserId());
        assertEquals("alice", loginVO.getUsername());
        assertEquals("Alice", loginVO.getRealName());
        assertNotNull(loginVO.getToken());
        assertFalse(loginVO.getToken().isBlank());
        verify(userMapper).updateToken(eq(9L), eq(loginVO.getToken()));
    }

    @Test
    void loginShouldUpgradeLegacyPasswordHash() {
        String rawPassword = "legacy-pass";
        SysUser user = new SysUser();
        user.setUserId(14L);
        user.setUsername("legacy");
        user.setRealName("Legacy");
        user.setPassword(sha256(rawPassword));

        LoginRequestVO vo = new LoginRequestVO();
        vo.setUsername("legacy");
        vo.setPassword(rawPassword);
        when(userMapper.selectByUsername("legacy")).thenReturn(user);

        LoginVO loginVO = userService.login(vo);

        assertNotNull(loginVO);
        assertFalse(loginVO.getToken().isBlank());
        verify(userMapper).updatePassword(eq(14L), anyString());
    }

    @Test
    void getByTokenShouldDelegateToMapper() {
        SysUser expectedUser = new SysUser();
        expectedUser.setUserId(18L);
        expectedUser.setUsername("token-user");
        when(userMapper.selectByToken("abc123")).thenReturn(expectedUser);

        SysUser actualUser = userService.getByToken("abc123");

        assertSame(expectedUser, actualUser);
    }

    private static RegisterVO buildRegisterVo(String username, String password, String confirmPassword, String realName) {
        RegisterVO vo = new RegisterVO();
        vo.setUsername(username);
        vo.setPassword(password);
        vo.setConfirmPassword(confirmPassword);
        vo.setRealName(realName);
        return vo;
    }

    private static String sha256(String input) {
        try {
            MessageDigest digest = MessageDigest.getInstance("SHA-256");
            return HexFormat.of().formatHex(digest.digest(input.getBytes(StandardCharsets.UTF_8)));
        } catch (Exception e) {
            throw new IllegalStateException("SHA-256 unavailable", e);
        }
    }
}
