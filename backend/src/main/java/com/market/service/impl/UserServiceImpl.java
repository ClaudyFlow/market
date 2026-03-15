package com.market.service.impl;

import com.market.dto.AuthResponse;
import com.market.dto.LoginRequest;
import com.market.dto.RegisterRequest;
import com.market.entity.*;
import com.market.repository.UserRepository;
import com.market.service.UserService;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.security.Keys;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import javax.crypto.SecretKey;
import java.nio.charset.StandardCharsets;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.*;

/**
 * 用户服务实现类
 */
@Service
public class UserServiceImpl implements UserService {

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private PasswordEncoder passwordEncoder;

    @Value("${jwt.secret:market-platform-secret-key-2024}")
    private String jwtSecret;

    @Value("${jwt.expiration:86400000}")
    private Long jwtExpiration;

    // 每日签到基础积分
    private static final int DAILY_CHECKIN_POINTS = 10;

    // VIP 等级配置
    private static final String[] VIP_LEVEL_NAMES = {
        "普通会员", "白银会员", "黄金会员", "铂金会员", "钻石会员", "至尊会员"
    };

    @Override
    @Transactional
    public AuthResponse register(RegisterRequest request) {
        // 验证密码
        if (!request.getPassword().equals(request.getConfirmPassword())) {
            return AuthResponse.failure("两次输入的密码不一致");
        }

        // 检查用户名是否存在
        if (userRepository.existsByName(request.getName())) {
            return AuthResponse.failure("用户名已存在");
        }

        // 检查邮箱是否存在
        if (userRepository.existsByEmail(request.getEmail())) {
            return AuthResponse.failure("邮箱已被注册");
        }

        // 创建新用户
        User user = new User();
        user.setName(request.getName());
        user.setEmail(request.getEmail());
        user.setPasswordHash(passwordEncoder.encode(request.getPassword()));
        user.setPoints(0);
        user.setTotalPoints(0);

        userRepository.save(user);

        // 生成 token
        String token = generateToken(user);

        AuthResponse.UserDTO userDTO = new AuthResponse.UserDTO(
            user.getId(), user.getName(), user.getEmail(), user.getAvatarUrl()
        );

        return AuthResponse.success("注册成功", token, userDTO);
    }

    @Override
    public AuthResponse login(LoginRequest request) {
        // 查找用户
        User user = userRepository.findByName(request.getName())
            .orElseGet(() -> userRepository.findByEmail(request.getName()).orElse(null));

        if (user == null) {
            return AuthResponse.failure("用户名或密码错误");
        }

        // 验证密码
        if (!passwordEncoder.matches(request.getPassword(), user.getPasswordHash())) {
            return AuthResponse.failure("用户名或密码错误");
        }

        // 生成 token
        String token = generateToken(user);

        AuthResponse.UserDTO userDTO = new AuthResponse.UserDTO(
            user.getId(), user.getName(), user.getEmail(), user.getAvatarUrl()
        );

        return AuthResponse.success("登录成功", token, userDTO);
    }

    @Override
    public VipInfo getVipInfo(Long userId) {
        User user = userRepository.findById(userId).orElse(null);
        if (user == null) {
            return null;
        }

        VipInfo vipInfo = new VipInfo();
        vipInfo.setLevel(0);
        vipInfo.setLevelName(VIP_LEVEL_NAMES[0]);
        vipInfo.setBenefits(Collections.singletonList("基础购物"));
        return vipInfo;
    }

    @Override
    public UserPointsInfo getUserPointsInfo(Long userId) {
        User user = userRepository.findById(userId).orElse(null);
        if (user == null) {
            return null;
        }

        UserPointsInfo pointsInfo = new UserPointsInfo();
        pointsInfo.setPoints(user.getPoints());
        pointsInfo.setTotalPoints(user.getTotalPoints());
        pointsInfo.setConsumedPoints(0);
        pointsInfo.setHasCheckedIn(false);
        pointsInfo.setConsecutiveDays(0);
        return pointsInfo;
    }

    @Override
    public User getUserById(Long userId) {
        return userRepository.findById(userId).orElse(null);
    }

    @Override
    @Transactional
    public CheckInResult checkIn(Long userId) {
        User user = userRepository.findById(userId).orElse(null);
        if (user == null) {
            CheckInResult result = new CheckInResult();
            result.setSuccess(false);
            result.setMessage("用户不存在");
            return result;
        }

        LocalDate today = LocalDate.now();
        LocalDate lastCheckInDate = null;
        if (user.getUpdatedAt() != null) {
            lastCheckInDate = user.getUpdatedAt().toLocalDate();
        }

        CheckInResult result = new CheckInResult();
        if (lastCheckInDate != null && lastCheckInDate.equals(today)) {
            result.setSuccess(false);
            result.setHasCheckedIn(true);
            result.setMessage("今日已签到");
            return result;
        }

        int points = DAILY_CHECKIN_POINTS;
        user.setPoints(user.getPoints() + points);
        user.setTotalPoints(user.getTotalPoints() + points);
        userRepository.save(user);

        result.setSuccess(true);
        result.setPoints(points);
        result.setHasCheckedIn(false);
        result.setConsecutiveDays(1);
        result.setMessage("签到成功，获得 " + points + " 积分");
        return result;
    }

    @Override
    @Transactional
    public boolean consumePoints(Long userId, Integer amount) {
        User user = userRepository.findById(userId).orElse(null);
        if (user == null || user.getPoints() < amount) {
            return false;
        }
        user.setPoints(user.getPoints() - amount);
        userRepository.save(user);
        return true;
    }

    @Override
    @Transactional
    public boolean addPoints(Long userId, Integer amount) {
        User user = userRepository.findById(userId).orElse(null);
        if (user == null) {
            return false;
        }
        user.setPoints(user.getPoints() + amount);
        user.setTotalPoints(user.getTotalPoints() + amount);
        userRepository.save(user);
        return true;
    }

    /**
     * 生成 JWT Token
     */
    private String generateToken(User user) {
        SecretKey key = Keys.hmacShaKeyFor(jwtSecret.getBytes(StandardCharsets.UTF_8));
        return Jwts.builder()
            .subject(user.getId().toString())
            .claim("username", user.getName())
            .claim("email", user.getEmail())
            .issuedAt(new Date())
            .expiration(new Date(System.currentTimeMillis() + jwtExpiration))
            .signWith(key)
            .compact();
    }
}
