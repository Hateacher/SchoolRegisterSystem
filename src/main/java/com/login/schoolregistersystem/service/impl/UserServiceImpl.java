package com.login.schoolregistersystem.service.impl;

import com.login.schoolregistersystem.common.BusinessException;
import com.login.schoolregistersystem.entity.SysUser;
import com.login.schoolregistersystem.mapper.UserMapper;
import com.login.schoolregistersystem.service.UserService;
import com.login.schoolregistersystem.vo.LoginRequestVO;
import com.login.schoolregistersystem.vo.LoginVO;
import com.login.schoolregistersystem.vo.RegisterVO;
import lombok.RequiredArgsConstructor;
import org.springframework.dao.DuplicateKeyException;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.nio.charset.StandardCharsets;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.util.HexFormat;
import java.util.UUID;

@Service
@RequiredArgsConstructor
public class UserServiceImpl implements UserService {

    private static final PasswordEncoder PASSWORD_ENCODER = new BCryptPasswordEncoder();

    private final UserMapper userMapper;

    @Override
    public Long register(RegisterVO vo) {
        if (userMapper.selectByUsername(vo.getUsername()) != null) {
            throw new BusinessException("用户名已存在");
        }
        if (!vo.getPassword().equals(vo.getConfirmPassword())) {
            throw new BusinessException("两次输入的密码不一致");
        }
        SysUser user = new SysUser();
        user.setUsername(vo.getUsername());
        user.setPassword(PASSWORD_ENCODER.encode(vo.getPassword()));
        user.setRealName(vo.getRealName());
        try {
            userMapper.insert(user);
        } catch (DuplicateKeyException e) {
            // 并发注册同名用户名时，由数据库唯一约束兜底，转为业务提示
            throw new BusinessException("用户名已存在");
        }
        return user.getUserId();
    }

    @Override
    public LoginVO login(LoginRequestVO vo) {
        SysUser user = userMapper.selectByUsername(vo.getUsername());
        if (user == null) {
            throw new BusinessException("用户不存在");
        }
        if (!matchesPassword(vo.getPassword(), user.getPassword())) {
            throw new BusinessException("密码错误");
        }
        if (needsPasswordUpgrade(user.getPassword())) {
            user.setPassword(PASSWORD_ENCODER.encode(vo.getPassword()));
            userMapper.updatePassword(user.getUserId(), user.getPassword());
        }
        String token = UUID.randomUUID().toString().replace("-", "");
        userMapper.updateToken(user.getUserId(), token);

        LoginVO loginVO = new LoginVO();
        loginVO.setToken(token);
        loginVO.setUserId(user.getUserId());
        loginVO.setUsername(user.getUsername());
        loginVO.setRealName(user.getRealName());
        return loginVO;
    }

    @Override
    public SysUser getByToken(String token) {
        return userMapper.selectByToken(token);
    }

    private static boolean matchesPassword(String rawPassword, String storedPassword) {
        if (storedPassword == null || storedPassword.isBlank()) {
            return false;
        }
        if (needsPasswordUpgrade(storedPassword)) {
            return sha256(rawPassword).equalsIgnoreCase(storedPassword);
        }
        return PASSWORD_ENCODER.matches(rawPassword, storedPassword);
    }

    private static boolean needsPasswordUpgrade(String storedPassword) {
        return storedPassword != null && !storedPassword.startsWith("$2");
    }

    /** SHA-256 摘要（仅用于兼容数据库中已存在的旧明文/无盐哈希值） */
    private static String sha256(String raw) {
        try {
            MessageDigest digest = MessageDigest.getInstance("SHA-256");
            return HexFormat.of().formatHex(digest.digest(raw.getBytes(StandardCharsets.UTF_8)));
        } catch (NoSuchAlgorithmException e) {
            throw new IllegalStateException("SHA-256 算法不可用", e);
        }
    }
}
