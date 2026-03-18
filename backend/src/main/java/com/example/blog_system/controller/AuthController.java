package com.example.blog_system.controller;

import com.example.blog_system.dto.LoginRequest;
import com.example.blog_system.entity.User;
import com.example.blog_system.service.UserService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import com.example.blog_system.security.JwtUtil;
import java.util.Map;

@RestController
@RequestMapping("/api/auth")
@RequiredArgsConstructor
public class AuthController {

    private final UserService userService;
    private final PasswordEncoder passwordEncoder;
    private final JwtUtil jwtUtil; // 注入 JwtUtil Bean
    @PostMapping("/login")
    public ResponseEntity<?> login(@RequestBody @Valid LoginRequest request) {
        try {
            User user = userService.findByUsername(request.getUsername());

            // 验证密码
            if (!passwordEncoder.matches(request.getPassword(), user.getPassword())) {
                throw new BadCredentialsException("用户名或密码错误");
            }

            // 生成 Token
            String token = jwtUtil.generateToken(user.getUsername());
            return ResponseEntity.ok(Map.of("token", token));

        } catch (BadCredentialsException | UsernameNotFoundException e) {
            // 统一返回 401，避免泄露用户是否存在
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED)
                    .body(Map.of("error", "用户名或密码错误"));
        }
    }
    @PostMapping("/register")
    public ResponseEntity<String> register(@RequestBody User user) {
        if (userService.existsByUsername(user.getUsername())) {
            return ResponseEntity.badRequest().body("用户名已存在");
        }
        user.setPassword(passwordEncoder.encode(user.getPassword()));
        userService.save(user);
        return ResponseEntity.ok("注册成功");
    }
}
