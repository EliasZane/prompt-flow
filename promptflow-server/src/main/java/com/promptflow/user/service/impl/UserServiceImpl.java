package com.promptflow.user.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.promptflow.common.exception.BusinessException;
import com.promptflow.common.utils.JwtUtils;
import com.promptflow.common.utils.SmsUtils;
import com.promptflow.user.mapper.SmsCodeMapper;
import com.promptflow.user.mapper.UserMapper;
import com.promptflow.user.model.dto.LoginRequest;
import com.promptflow.user.model.dto.RegisterRequest;
import com.promptflow.user.model.dto.SmsSendRequest;
import com.promptflow.user.model.entity.SmsCodeEntity;
import com.promptflow.user.model.entity.UserEntity;
import com.promptflow.user.service.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;

import java.time.LocalDateTime;
import java.util.Random;

@Service
public class UserServiceImpl implements UserService {

    @Autowired
    private UserMapper userMapper;

    @Autowired
    private SmsCodeMapper smsCodeMapper;

    @Autowired
    private SmsUtils smsUtils;

    @Autowired
    private JwtUtils jwtUtils;

    private final BCryptPasswordEncoder passwordEncoder = new BCryptPasswordEncoder();

    @Override
    public UserEntity register(RegisterRequest request) {
        // 1. 校验验证码
        verifySmsCode(request.getPhone(), request.getCode(), "REGISTER");

        // 2. 检查用户名和手机号
        if (userMapper.selectByUsername(request.getUsername()) != null) {
            throw new BusinessException("用户名已存在");
        }
        if (userMapper.selectOne(new LambdaQueryWrapper<UserEntity>().eq(UserEntity::getPhone, request.getPhone())) != null) {
            throw new BusinessException("手机号已被注册");
        }

        // 3. 创建用户
        UserEntity user = new UserEntity();
        user.setUsername(request.getUsername());
        user.setPhone(request.getPhone());
        user.setPasswordHash(passwordEncoder.encode(request.getPassword()));
        user.setRemainingCount(3); // 系统赠送3次
        user.setTotalUsedCount(0);
        user.setCreatedAt(LocalDateTime.now());
        user.setUpdatedAt(LocalDateTime.now());
        userMapper.insert(user);

        return user;
    }

    @Override
    public UserEntity login(LoginRequest request) {
        UserEntity user;
        if ("SMS".equals(request.getLoginType())) {
            verifySmsCode(request.getPhone(), request.getCode(), "LOGIN");
            user = userMapper.selectOne(new LambdaQueryWrapper<UserEntity>().eq(UserEntity::getPhone, request.getPhone()));
            if (user == null) {
                throw new BusinessException("手机号未注册");
            }
        } else {
            user = userMapper.selectByUsername(request.getUsername());
            if (user == null) {
                user = userMapper.selectOne(new LambdaQueryWrapper<UserEntity>().eq(UserEntity::getPhone, request.getUsername()));
            }
            if (user == null) {
                throw new BusinessException("用户不存在");
            }
            if (!passwordEncoder.matches(request.getPassword(), user.getPasswordHash())) {
                throw new BusinessException("密码错误");
            }
        }
        return user;
    }

    @Override
    public void sendSmsCode(SmsSendRequest request) {
        String phone = request.getPhone();
        String scene = request.getScene();

        // 1. 基础校验
        if (phone == null || !phone.matches("^1[3-9]\\d{9}$")) {
            throw new BusinessException("请输入正确的手机号");
        }

        // 2. 业务预校验
        if ("REGISTER".equals(scene)) {
            if (request.getUsername() == null || request.getUsername().length() < 2) {
                throw new BusinessException("用户名长度至少为2位");
            }
            if (userMapper.selectByUsername(request.getUsername()) != null) {
                throw new BusinessException("用户名已存在");
            }
            if (userMapper.selectOne(new LambdaQueryWrapper<UserEntity>().eq(UserEntity::getPhone, phone)) != null) {
                throw new BusinessException("手机号已被注册");
            }
        } else if ("LOGIN".equals(scene) || "RESET_PWD".equals(scene)) {
            if (userMapper.selectOne(new LambdaQueryWrapper<UserEntity>().eq(UserEntity::getPhone, phone)) == null) {
                throw new BusinessException("该手机号未注册");
            }
        }

        // 3. 频率限制：
        // a) 严格限制：60秒内只能发送1次（匹配阿里云默认限制）
        SmsCodeEntity lastSms = smsCodeMapper.selectOne(new LambdaQueryWrapper<SmsCodeEntity>()
                .eq(SmsCodeEntity::getPhone, phone)
                .gt(SmsCodeEntity::getCreatedAt, LocalDateTime.now().minusSeconds(60))
                .orderByDesc(SmsCodeEntity::getCreatedAt)
                .last("LIMIT 1"));
        if (lastSms != null) {
            throw new BusinessException("验证码发送太频繁，请60秒后再试");
        }

        // b) 累计限制：1小时内最多发送5次，防止恶意刷短信
        long hourCount = smsCodeMapper.selectCount(new LambdaQueryWrapper<SmsCodeEntity>()
                .eq(SmsCodeEntity::getPhone, phone)
                .gt(SmsCodeEntity::getCreatedAt, LocalDateTime.now().minusHours(1)));
        if (hourCount >= 5) {
            throw new BusinessException("今天发送验证码次数过多，请稍后再试");
        }

        // 4. 生成6位数字验证码
        String code = String.format("%06d", new Random().nextInt(1000000));

        // 5. 保存到数据库
        SmsCodeEntity smsCode = new SmsCodeEntity();
        smsCode.setPhone(phone);
        smsCode.setCode(code);
        smsCode.setScene(scene);
        smsCode.setUsed(0);
        smsCode.setExpireTime(LocalDateTime.now().plusMinutes(5)); // 5分钟过期
        smsCode.setCreatedAt(LocalDateTime.now());
        smsCode.setIp(request.getIp());
        smsCodeMapper.insert(smsCode);

        // 6. 发送短信
        smsUtils.sendSms(phone, code);
    }

    @Override
    public void resetPassword(String phone, String code, String newPassword) {
        verifySmsCode(phone, code, "RESET_PWD");
        UserEntity user = userMapper.selectOne(new LambdaQueryWrapper<UserEntity>().eq(UserEntity::getPhone, phone));
        if (user == null) {
            throw new BusinessException("手机号未注册");
        }
        user.setPasswordHash(passwordEncoder.encode(newPassword));
        user.setUpdatedAt(LocalDateTime.now());
        userMapper.updateById(user);
    }

    private void verifySmsCode(String phone, String code, String scene) {
        SmsCodeEntity smsCode = smsCodeMapper.selectOne(new LambdaQueryWrapper<SmsCodeEntity>()
                .eq(SmsCodeEntity::getPhone, phone)
                .eq(SmsCodeEntity::getScene, scene)
                .eq(SmsCodeEntity::getUsed, 0)
                .orderByDesc(SmsCodeEntity::getCreatedAt)
                .last("LIMIT 1"));

        if (smsCode == null || !smsCode.getCode().equals(code)) {
            throw new BusinessException("验证码错误");
        }
        if (smsCode.getExpireTime().isBefore(LocalDateTime.now())) {
            throw new BusinessException("验证码已过期");
        }

        // 标记为已使用
        smsCode.setUsed(1);
        smsCodeMapper.updateById(smsCode);
    }

    @Override
    public UserEntity findByUsername(String username) {
        return userMapper.selectByUsername(username);
    }

    @Override
    public UserEntity findByPhone(String phone) {
        return userMapper.selectOne(new LambdaQueryWrapper<UserEntity>().eq(UserEntity::getPhone, phone));
    }
}
