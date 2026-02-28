package com.example.blog_system.service;

import com.example.blog_system.entity.User;
import com.example.blog_system.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor // 使用构造器注入
public class UserService {

    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder; // 虽然注册时加密已在 Controller 做，但 Service 可能也需要（如修改密码）

    /**
     * 检查用户名是否存在
     */
    public boolean existsByUsername(String username) {
        return userRepository.existsByUsername(username);
    }

    /**
     * 保存用户（通常由 Controller 调用）
     */
    @Transactional
    public User save(User user) {
        // 可选：再次校验（防御性编程）
        if (existsByUsername(user.getUsername())) {
            throw new IllegalArgumentException("用户名已存在");
        }
        return userRepository.save(user);
    }

    /**
     * 根据用户名查找用户（用于登录认证）
     */
    public User findByUsername(String username) {
        return userRepository.findByUsername(username)
                .orElseThrow(() -> new RuntimeException("用户不存在: " + username));
    }
}
