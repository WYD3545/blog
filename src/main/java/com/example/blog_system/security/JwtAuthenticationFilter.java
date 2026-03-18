package com.example.blog_system.security;

import com.example.blog_system.service.UserService;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.lang.NonNull;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.web.authentication.WebAuthenticationDetailsSource;
import org.springframework.stereotype.Component;
import org.springframework.util.StringUtils;
import org.springframework.web.filter.OncePerRequestFilter;

import java.io.IOException;

@Component
@RequiredArgsConstructor
public class JwtAuthenticationFilter extends OncePerRequestFilter {

    private final JwtUtil jwtUtil;
    private final UserService userService;

    @Override
    protected void doFilterInternal(
            @NonNull HttpServletRequest request,
            @NonNull HttpServletResponse response,
            @NonNull FilterChain filterChain) throws ServletException, IOException {

        String authHeader = request.getHeader("Authorization");

        if (!StringUtils.hasText(authHeader) || !authHeader.startsWith("Bearer ")) {
            System.out.println("[JWT Filter] 未找到有效的 Authorization 头");
            filterChain.doFilter(request, response);
            return;
        }

        String token = authHeader.substring(7);
        System.out.println("[JWT Filter] 获取到 token: " + token.substring(0, Math.min(20, token.length())) + "...");

        String username = jwtUtil.extractUsername(token);
        System.out.println("[JWT Filter] 从 token 中解析的用户名：" + username);

        if (StringUtils.hasText(username) &&
                SecurityContextHolder.getContext().getAuthentication() == null) {

            try {
                UserDetails userDetails = userService.loadUserByUsername(username);
                System.out.println("[JWT Filter] 成功加载用户详情：" + username);

                boolean isValid = jwtUtil.validateToken(token, username);
                System.out.println("[JWT Filter] Token 验证结果：" + isValid);

                if (isValid) {
                    UsernamePasswordAuthenticationToken authToken =
                            new UsernamePasswordAuthenticationToken(
                                    userDetails,
                                    null,
                                    userDetails.getAuthorities()
                            );

                    authToken.setDetails(
                            new WebAuthenticationDetailsSource().buildDetails(request)
                    );

                    SecurityContextHolder.getContext().setAuthentication(authToken);
                    System.out.println("[JWT Filter] 认证成功，设置 SecurityContext");
                }
            } catch (Exception e) {
                System.out.println("[JWT Filter] 认证过程发生异常：" + e.getMessage());
                e.printStackTrace();
            }
        }

        filterChain.doFilter(request, response);
    }
}
