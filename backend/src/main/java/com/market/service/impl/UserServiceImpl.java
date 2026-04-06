package com.market.service.impl;

import com.market.dto.AuthResponse;
import com.market.dto.LoginRequest;
import com.market.dto.RegisterRequest;
import com.market.dto.MerchantRegisterRequest;
import com.market.entity.*;
import com.market.repository.OrderRepository;
import com.market.repository.ProductReviewRepository;
import com.market.repository.ShopRepository;
import com.market.repository.UserRepository;
import com.market.security.JwtService;
import com.market.service.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.Pageable;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.*;
import java.time.LocalDateTime;
import java.util.stream.Collectors;

/**
 * 用户服务实现类
 */
@Service
public class UserServiceImpl implements UserService {

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private JwtService jwtService;

    @Autowired
    private PasswordEncoder passwordEncoder;

    @Autowired
    private ProductReviewRepository productReviewRepository;

    @Autowired
    private OrderRepository orderRepository;

    @Autowired
    private ShopRepository shopRepository;

    // 每日签到基础积分
    private static final int DAILY_CHECKIN_CREDIT = 10;

    // VIP 等级配置
    private static final String[] VIP_LEVEL_NAMES = {
            "普通会员", "白银会员", "黄金会员", "铂金会员", "钻石会员", "至尊会员"
    };

    // VIP 权益配置
    private static final Map<Integer, List<String>> VIP_BENEFITS = new HashMap<>();
    static {
        VIP_BENEFITS.put(0, Arrays.asList("基础购物"));
        VIP_BENEFITS.put(1, Arrays.asList("基础购物", "生日礼包"));
        VIP_BENEFITS.put(2, Arrays.asList("基础购物", "生日礼包", "运费券"));
        VIP_BENEFITS.put(3, Arrays.asList("基础购物", "生日礼包", "运费券", "专属客服"));
        VIP_BENEFITS.put(4, Arrays.asList("基础购物", "生日礼包", "运费券", "专属客服", "折扣优惠"));
        VIP_BENEFITS.put(5, Arrays.asList("基础购物", "生日礼包", "运费券", "专属客服", "折扣优惠", "优先发货"));
    }

    @Override
    public AuthResponse register(RegisterRequest request) {
        // 检查用户名是否已存在
        if (userRepository.existsByName(request.getName())) {
            return AuthResponse.failure("用户名已存在");
        }

        // 检查邮箱是否已存在（如果有提供）
        if (request.getEmail() != null && !request.getEmail().isEmpty() &&
                userRepository.existsByEmail(request.getEmail())) {
            return AuthResponse.failure("邮箱已被使用");
        }

        // 创建新用户
        User user = new User(request.getName(), request.getEmail(),
                passwordEncoder.encode(request.getPassword()));
        user = userRepository.save(user);

        // 生成 JWT token
        String token = jwtService.generateToken(user);

        return AuthResponse.success("注册成功", token, convertToUserDTO(user));
    }

    @Override
    public AuthResponse registerMerchant(MerchantRegisterRequest request) {
        // 检查用户名是否已存在
        if (userRepository.existsByName(request.getName())) {
            return AuthResponse.failure("用户名已存在");
        }

        // 检查邮箱是否已存在
        if (request.getEmail() != null && !request.getEmail().isEmpty() &&
                userRepository.existsByEmail(request.getEmail())) {
            return AuthResponse.failure("邮箱已被使用");
        }

        // 检查店铺名称是否已存在
        if (userRepository.existsByShopName(request.getShopName())) {
            return AuthResponse.failure("店铺名称已存在");
        }

        // 验证密码
        if (!request.getPassword().equals(request.getConfirmPassword())) {
            return AuthResponse.failure("两次输入的密码不一致");
        }

        // 创建商家用户
        User merchant = new User(request.getName(), request.getEmail(),
                passwordEncoder.encode(request.getPassword()));
        merchant.setIsMerchant(true);
        merchant.setShopName(request.getShopName());
        merchant.setShopDescription(request.getShopDescription());
        merchant.setPhone(request.getPhone());
        merchant.setStatus("ACTIVE"); // 默认启用
        merchant = userRepository.save(merchant);

        // 生成 JWT token
        String token = jwtService.generateToken(merchant);

        return AuthResponse.success("商家注册成功", token, convertToUserDTO(merchant));
    }

    @Override
    @Transactional
    public boolean addCredit(Long userId, Integer amount) {
        User user = userRepository.findById(userId).orElse(null);
        if (user == null) {
            return false;
        }

        user.setCredit(user.getCredit() + amount);
        user.setTotalCredit(user.getTotalCredit() + amount);
        userRepository.save(user);
        return true;
    }

    @Override
    public AuthResponse login(LoginRequest request) {
        // 根据用户名或邮箱查找用户
        User user = userRepository.findByName(request.getName())
                .orElseGet(() -> userRepository.findByEmail(request.getName()).orElse(null));

        if (user == null) {
            return AuthResponse.failure("用户名或密码错误");
        }

        // 验证密码
        if (!passwordEncoder.matches(request.getPassword(), user.getPasswordHash())) {
            return AuthResponse.failure("用户名或密码错误");
        }

        // 生成 JWT token
        String token = jwtService.generateToken(user);

        return AuthResponse.success("登录成功", token, convertToUserDTO(user));
    }

    @Override
    public VipInfo getVipInfo(Long userId) {
        User user = userRepository.findById(userId).orElse(null);
        if (user == null) {
            return null;
        }

        VipInfo vipInfo = new VipInfo();
        vipInfo.setLevel(user.getVipLevel());
        vipInfo.setLevelName(VIP_LEVEL_NAMES[user.getVipLevel()]);
        vipInfo.setExpireTime(user.getVipExpireTime());
        vipInfo.setBenefits(VIP_BENEFITS.getOrDefault(user.getVipLevel(), VIP_BENEFITS.get(0)));
        vipInfo.setGrowthValue(user.getGrowthValue());
        vipInfo.setNextLevelGrowth(calculateNextLevelGrowth(user.getVipLevel()));

        return vipInfo;
    }

    @Override
    public UserCreditInfo getUserCreditInfo(Long userId) {
        User user = userRepository.findById(userId).orElse(null);
        if (user == null) {
            return null;
        }

        UserCreditInfo creditInfo = new UserCreditInfo();
        creditInfo.setCredit(user.getCredit());
        creditInfo.setTotalCredit(user.getTotalCredit());
        creditInfo.setConsumedCredit(user.getConsumedCredit());

        // 检查今日是否已签到
        boolean hasCheckedIn = hasCheckedInToday(user.getLastCheckInTime());
        creditInfo.setHasCheckedIn(hasCheckedIn);
        creditInfo.setConsecutiveDays(user.getConsecutiveCheckinDays());
        creditInfo.setLastCheckInTime(user.getLastCheckInTime());

        return creditInfo;
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

        // 检查今日是否已签到
        if (hasCheckedInToday(user.getLastCheckInTime())) {
            CheckInResult result = new CheckInResult();
            result.setSuccess(false);
            result.setHasCheckedIn(true);
            result.setMessage("今日已签到");
            return result;
        }

        // 计算连续签到天数
        int consecutiveDays = calculateConsecutiveDays(user.getLastCheckInTime(), user.getConsecutiveCheckinDays());

        // 计算奖励积分（连续签到有额外奖励）
        int bonusCredit = calculateBonusCredit(consecutiveDays);
        int totalCredit = DAILY_CHECKIN_CREDIT + bonusCredit;

        // 更新用户积分
        user.setCredit(user.getCredit() + totalCredit);
        user.setTotalCredit(user.getTotalCredit() + totalCredit);
        user.setConsecutiveCheckinDays(consecutiveDays);
        user.setLastCheckInTime(new Date());

        // 更新成长值
        user.setGrowthValue(user.getGrowthValue() + 10);

        // 检查是否需要升级 VIP
        updateVipLevel(user);

        userRepository.save(user);

        CheckInResult result = new CheckInResult();
        result.setSuccess(true);
        result.setCredit(totalCredit);
        result.setHasCheckedIn(true);
        result.setConsecutiveDays(consecutiveDays);
        result.setMessage(String.format("签到成功！获得 %d 积分，连续签到 %d 天", totalCredit, consecutiveDays));

        return result;
    }

    @Override
    @Transactional
    public boolean consumeCredit(Long userId, Integer amount) {
        User user = userRepository.findById(userId).orElse(null);
        if (user == null || user.getCredit() < amount) {
            return false;
        }

        user.setCredit(user.getCredit() - amount);
        user.setConsumedCredit(user.getConsumedCredit() + amount);
        userRepository.save(user);
        return true;
    }

    @Override
    public Page<User> getAllUsers(String userId, String userName, String phone, String status,
                                  LocalDateTime startDate, LocalDateTime endDate, Pageable pageable) {
        List<User> allUsers = userRepository.findAll().stream()
            .filter(u -> userId == null || userId.isEmpty() || u.getId().toString().contains(userId))
            .filter(u -> userName == null || userName.isEmpty() || u.getName().toLowerCase().contains(userName.toLowerCase()))
            .filter(u -> phone == null || phone.isEmpty() || (u.getPhone() != null && u.getPhone().contains(phone)))
            .filter(u -> status == null || status.isEmpty() || u.getStatus().equals(status))
            .filter(u -> startDate == null || u.getCreatedAt().isAfter(startDate))
            .filter(u -> endDate == null || u.getCreatedAt().isBefore(endDate))
            .sorted(Comparator.comparing(User::getCreatedAt).reversed())
            .collect(Collectors.toList());

        int start = (int) pageable.getOffset() * pageable.getPageSize();
        int end = Math.min(start + pageable.getPageSize(), allUsers.size());
        List<User> pageContent = allUsers.subList(start, end);

        return new PageImpl<>(pageContent, pageable, allUsers.size());
    }

    @Override
    @Transactional
    public User createUser(User user) {
        if (userRepository.existsByName(user.getName())) {
            throw new RuntimeException("用户名已存在");
        }
        if (user.getEmail() != null && userRepository.existsByEmail(user.getEmail())) {
            throw new RuntimeException("邮箱已被使用");
        }
        if (user.getPasswordHash() != null) {
            user.setPasswordHash(passwordEncoder.encode(user.getPasswordHash()));
        }
        return userRepository.save(user);
    }

    @Override
    @Transactional
    public User updateUser(Long id, Map<String, Object> updates) {
        User user = userRepository.findById(id)
            .orElseThrow(() -> new RuntimeException("用户不存在"));

        if (updates.containsKey("name")) {
            user.setName((String) updates.get("name"));
        }
        if (updates.containsKey("email")) {
            user.setEmail((String) updates.get("email"));
        }
        if (updates.containsKey("phone")) {
            user.setPhone((String) updates.get("phone"));
        }
        if (updates.containsKey("avatar")) {
            user.setAvatarUrl((String) updates.get("avatar"));
        }
        if (updates.containsKey("avatarUrl")) {
            user.setAvatarUrl((String) updates.get("avatarUrl"));
        }
        if (updates.containsKey("credit")) {
            user.setCredit((Integer) updates.get("credit"));
        }
        if (updates.containsKey("status")) {
            user.setStatus((String) updates.get("status"));
        }
        if (updates.containsKey("vipLevel")) {
            user.setVipLevel((Integer) updates.get("vipLevel"));
        }
        if (updates.containsKey("growthValue")) {
            user.setGrowthValue((Integer) updates.get("growthValue"));
        }

        user.setUpdatedAt(LocalDateTime.now());
        return userRepository.save(user);
    }

    @Override
    @Transactional
    public void deleteUser(Long id) {
        userRepository.deleteById(id);
    }

    @Override
    @Transactional
    public User banUser(Long id) {
        User user = userRepository.findById(id)
            .orElseThrow(() -> new RuntimeException("用户不存在"));
        user.setStatus("BANNED");
        user.setUpdatedAt(LocalDateTime.now());
        return userRepository.save(user);
    }

    @Override
    @Transactional
    public User unbanUser(Long id) {
        User user = userRepository.findById(id)
            .orElseThrow(() -> new RuntimeException("用户不存在"));
        user.setStatus("ACTIVE");
        user.setUpdatedAt(LocalDateTime.now());
        return userRepository.save(user);
    }

    @Override
    public Map<String, Object> getUserStats() {
        List<User> allUsers = userRepository.findAll();
        long totalUsers = allUsers.size();
        long activeUsers = allUsers.stream().filter(u -> "ACTIVE".equals(u.getStatus())).count();
        long bannedUsers = allUsers.stream().filter(u -> "BANNED".equals(u.getStatus())).count();

        LocalDateTime todayStart = LocalDateTime.now().toLocalDate().atStartOfDay();
        long todayNewUsers = allUsers.stream()
            .filter(u -> u.getCreatedAt().isAfter(todayStart))
            .count();

        Map<String, Object> stats = new HashMap<>();
        stats.put("total", totalUsers);
        stats.put("active", activeUsers);
        stats.put("banned", bannedUsers);
        stats.put("todayNew", todayNewUsers);

        return stats;
    }

    @Override
    public Page<User> getAllMerchants(String merchantId, String shopName, String status,
                                      LocalDateTime startDate, LocalDateTime endDate, Pageable pageable) {
        List<User> allMerchants = userRepository.findAll().stream()
            .filter(u -> u.getIsMerchant() != null && u.getIsMerchant())
            .filter(u -> merchantId == null || merchantId.isEmpty() || u.getId().toString().contains(merchantId))
            .filter(u -> shopName == null || shopName.isEmpty() || 
                (u.getShopName() != null && u.getShopName().toLowerCase().contains(shopName.toLowerCase())))
            .filter(u -> status == null || status.isEmpty() || u.getMerchantStatus().equals(status))
            .filter(u -> startDate == null || u.getCreatedAt().isAfter(startDate))
            .filter(u -> endDate == null || u.getCreatedAt().isBefore(endDate))
            .sorted(Comparator.comparing(User::getCreatedAt).reversed())
            .collect(Collectors.toList());

        int start = (int) pageable.getOffset() * pageable.getPageSize();
        int end = Math.min(start + pageable.getPageSize(), allMerchants.size());
        List<User> pageContent = allMerchants.subList(start, end);

        return new PageImpl<>(pageContent, pageable, allMerchants.size());
    }

    @Override
    @Transactional
    public User banMerchant(Long id) {
        User merchant = userRepository.findById(id)
            .orElseThrow(() -> new RuntimeException("商家不存在"));
        merchant.setMerchantStatus("BANNED");
        merchant.setStatus("BANNED");
        merchant.setUpdatedAt(LocalDateTime.now());
        return userRepository.save(merchant);
    }

    @Override
    @Transactional
    public User unbanMerchant(Long id) {
        User merchant = userRepository.findById(id)
            .orElseThrow(() -> new RuntimeException("商家不存在"));
        merchant.setMerchantStatus("ACTIVE");
        merchant.setStatus("ACTIVE");
        merchant.setUpdatedAt(LocalDateTime.now());
        return userRepository.save(merchant);
    }

    @Override
    public Map<String, Object> getMerchantStats() {
        List<User> allMerchants = userRepository.findAll().stream()
            .filter(u -> u.getIsMerchant() != null && u.getIsMerchant())
            .collect(Collectors.toList());

        long totalMerchants = allMerchants.size();
        long approvedMerchants = allMerchants.stream()
            .filter(u -> "ACTIVE".equals(u.getMerchantStatus())).count();
        long pendingMerchants = allMerchants.stream()
            .filter(u -> "PENDING".equals(u.getMerchantStatus()) || "INACTIVE".equals(u.getMerchantStatus())).count();
        long rejectedMerchants = allMerchants.stream()
            .filter(u -> "REJECTED".equals(u.getMerchantStatus())).count();

        Map<String, Object> stats = new HashMap<>();
        stats.put("total", totalMerchants);
        stats.put("approved", approvedMerchants);
        stats.put("pending", pendingMerchants);
        stats.put("rejected", rejectedMerchants);

        return stats;
    }

    /**
     * 检查今日是否已签到
     */
    private boolean hasCheckedInToday(Date lastCheckInTime) {
        if (lastCheckInTime == null) {
            return false;
        }

        Calendar today = Calendar.getInstance();
        Calendar lastCheckIn = Calendar.getInstance();
        lastCheckIn.setTime(lastCheckInTime);

        return today.get(Calendar.YEAR) == lastCheckIn.get(Calendar.YEAR) &&
                today.get(Calendar.DAY_OF_YEAR) == lastCheckIn.get(Calendar.DAY_OF_YEAR);
    }

    /**
     * 计算连续签到天数
     */
    private int calculateConsecutiveDays(Date lastCheckInTime, int currentDays) {
        if (lastCheckInTime == null) {
            return 1;
        }

        Calendar today = Calendar.getInstance();
        Calendar lastCheckIn = Calendar.getInstance();
        lastCheckIn.setTime(lastCheckInTime);

        long diffDays = (today.getTimeInMillis() - lastCheckIn.getTimeInMillis()) / (1000 * 60 * 60 * 24);

        if (diffDays == 0) {
            // 今天已签到
            return currentDays;
        } else if (diffDays == 1) {
            // 昨天签到，连续
            return currentDays + 1;
        } else {
            // 中断，重新计算
            return 1;
        }
    }

    /**
     * 计算连续签到奖励积分
     */
    private int calculateBonusCredit(int consecutiveDays) {
        if (consecutiveDays >= 30) {
            return 20; // 连续 30 天奖励 20 积分
        } else if (consecutiveDays >= 14) {
            return 10; // 连续 14 天奖励 10 积分
        } else if (consecutiveDays >= 7) {
            return 5;  // 连续 7 天奖励 5 积分
        }
        return 0;
    }

    /**
     * 计算下一等级所需成长值
     */
    private int calculateNextLevelGrowth(int currentLevel) {
        if (currentLevel >= 5) {
            return 0; // 最高等级
        }
        return (currentLevel + 1) * 1000;
    }

    /**
     * 更新 VIP 等级
     */
    private void updateVipLevel(User user) {
        int growthValue = user.getGrowthValue();
        int newLevel = 0;

        if (growthValue >= 5000) {
            newLevel = 5;
        } else if (growthValue >= 4000) {
            newLevel = 4;
        } else if (growthValue >= 3000) {
            newLevel = 3;
        } else if (growthValue >= 2000) {
            newLevel = 2;
        } else if (growthValue >= 1000) {
            newLevel = 1;
        }

        if (newLevel > user.getVipLevel()) {
            user.setVipLevel(newLevel);
            // 设置 VIP 过期时间为一年后
            Calendar calendar = Calendar.getInstance();
            calendar.add(Calendar.YEAR, 1);
            user.setVipExpireTime(calendar.getTime());
        }
    }

    /**
     * 将 User 实体转换为 UserDTO
     */
    private AuthResponse.UserDTO convertToUserDTO(User user) {
        return new AuthResponse.UserDTO(
                user.getId(),
                user.getName(),
                user.getEmail(),
                user.getAvatarUrl()
        );
    }

    @Override
    public void updateMerchant(User merchant) {
        userRepository.save(merchant);
    }

    @Override
    public Map<String, Object> getMerchantShopStats(User merchant) {
        Map<String, Object> stats = new HashMap<>();
        
        // 查询店铺信息
        Shop shop = shopRepository.findByOwnerId(merchant.getId()).orElse(null);
        if (shop != null) {
            stats.put("shopId", shop.getId());
            stats.put("shopName", shop.getName());
            stats.put("rating", shop.getRating());
            stats.put("followers", shop.getFollowers());
        }
        
        // 查询订单统计
        long totalOrders = orderRepository.count();
        stats.put("orders", totalOrders);
        
        // 查询销售额（简化处理）
        stats.put("sales", "0");
        stats.put("visitors", 0);
        stats.put("favorites", shop != null ? shop.getFollowers() : 0);
        
        return stats;
    }
}
