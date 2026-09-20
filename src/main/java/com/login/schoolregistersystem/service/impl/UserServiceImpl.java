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
import org.springframework.stereotype.Service;

import java.nio.charset.StandardCharsets;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.util.HexFormat;
import java.util.UUID;

@Service
@RequiredArgsConstructor
public class UserServiceImpl implements UserService {

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
        user.setPassword(sha256(vo.getPassword()));
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
        if (!sha256(vo.getPassword()).equals(user.getPassword())) {
            throw new BusinessException("密码错误");
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

    /** SHA-256 摘要（无盐），与数据库 password 字段（VARCHAR 64）对应 */
    private static String sha256(String raw) {
        try {
            MessageDigest digest = MessageDigest.getInstance("SHA-256");
            return HexFormat.of().formatHex(digest.digest(raw.getBytes(StandardCharsets.UTF_8)));
        } catch (NoSuchAlgorithmException e) {
            throw new IllegalStateException("SHA-256 算法不可用", e);
        }
    }
}
