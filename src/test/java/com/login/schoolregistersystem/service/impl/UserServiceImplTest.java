package com.login.schoolregistersystem.service.impl;

import com.login.schoolregistersystem.entity.SysUser;
import com.login.schoolregistersystem.mapper.UserMapper;
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
import java.security.NoSuchAlgorithmException;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class UserServiceImplTest {

    @Mock
    private UserMapper userMapper;

    @InjectMocks
    private UserServiceImpl userService;

    @Test
    void register_shouldStoreBCryptHash() {
        when(userMapper.selectByUsername("alice")).thenReturn(null);

        RegisterVO vo = new RegisterVO();
        vo.setUsername("alice");
        vo.setPassword("123456");
        vo.setConfirmPassword("123456");
        vo.setRealName("Alice");

        userService.register(vo);

        ArgumentCaptor<SysUser> captor = ArgumentCaptor.forClass(SysUser.class);
        verify(userMapper).insert(captor.capture());

        String encodedPassword = captor.getValue().getPassword();
        assertThat(encodedPassword).startsWith("$2");
        assertThat(new BCryptPasswordEncoder().matches("123456", encodedPassword)).isTrue();
    }

    @Test
    void login_shouldAcceptLegacySha256AndUpgradeToBCrypt() throws NoSuchAlgorithmException {
        SysUser legacyUser = new SysUser();
        legacyUser.setUserId(7L);
        legacyUser.setUsername("legacy");
        legacyUser.setPassword(sha256("123456"));
        legacyUser.setRealName("Legacy");
        when(userMapper.selectByUsername("legacy")).thenReturn(legacyUser);

        LoginRequestVO vo = new LoginRequestVO();
        vo.setUsername("legacy");
        vo.setPassword("123456");

        LoginVO loginVO = userService.login(vo);

        assertThat(loginVO.getToken()).isNotBlank();
        ArgumentCaptor<String> passwordCaptor = ArgumentCaptor.forClass(String.class);
        verify(userMapper).updatePassword(eq(7L), passwordCaptor.capture());
        assertThat(passwordCaptor.getValue()).startsWith("$2");
        assertThat(new BCryptPasswordEncoder().matches("123456", passwordCaptor.getValue())).isTrue();
    }

    private static String sha256(String raw) throws NoSuchAlgorithmException {
        MessageDigest digest = MessageDigest.getInstance("SHA-256");
        byte[] bytes = digest.digest(raw.getBytes(StandardCharsets.UTF_8));
        StringBuilder sb = new StringBuilder();
        for (byte b : bytes) {
            sb.append(String.format("%02x", b));
        }
        return sb.toString();
    }
}
