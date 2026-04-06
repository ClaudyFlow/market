package com.market.security;

import com.market.entity.User;
import com.market.repository.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;

/**
 * UserDetailsService 实现类
 * 根据用户名或邮箱加载用户详情，供 Spring Security 认证流程使用
 *
 * @author market-team
 * @since 1.0
 */
@Service
public class UserDetailsServiceImpl implements UserDetailsService {

    @Autowired
    private UserRepository userRepository;

    /**
     * 根据用户名或邮箱加载用户信息
     *
     * @param username 用户名或邮箱
     * @return 用户详情对象
     * @throws UsernameNotFoundException 当用户不存在时抛出
     */
    @Override
    public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {
        User user = userRepository.findByName(username)
            .orElseGet(() -> userRepository.findByEmail(username).orElse(null));

        if (user == null) {
            throw new UsernameNotFoundException("用户不存在：" + username);
        }

        return user;
    }
}
