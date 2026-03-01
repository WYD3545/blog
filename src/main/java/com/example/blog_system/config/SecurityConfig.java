package com.example.blog_system.config;


import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;

@Configuration
@EnableWebSecurity
public class SecurityConfig {

    @Bean
    public PasswordEncoder passwordEncoder() {
        return new BCryptPasswordEncoder();
    }
    @Bean
    public SecurityFilterChain filterChain(HttpSecurity http) throws Exception {
        http
                .csrf(csrf -> csrf.disable()) // 禁用 CSRF（前后端分离常用）
                .sessionManagement(session -> session.sessionCreationPolicy(SessionCreationPolicy.STATELESS)) // 无状态（JWT 场景）
                .authorizeHttpRequests(authz -> authz
                        .anyRequest().permitAll() // 临时允许所有请求，用于调试
                );
        return http.build();
    }
//    @Bean
//    public SecurityFilterChain filterChain(HttpSecurity http) throws Exception {
//        http
//                .csrf(csrf -> csrf.disable()) // 禁用 CSRF（前后端分离常用）
//                .sessionManagement(session -> session.sessionCreationPolicy(SessionCreationPolicy.STATELESS)) // 无状态（JWT 场景）
//                .authorizeHttpRequests(authz -> authz
//                        .requestMatchers("/api/auth/**",
//                                    "/api/posts/**",
//                                    "/api/posts",
//                                    "/swagger-ui/**",
//                                    "/v3/api-docs/**",
//                                    "/swagger-resources/**",
//                                    "/webjars/**").permitAll() // 允许公开访问
//                        .anyRequest().authenticated() // 其他所有接口都需要认证
//                );
//        return http.build();
//    }
}
//该类用于密码加密配置