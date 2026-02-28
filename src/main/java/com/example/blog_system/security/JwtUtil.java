package com.example.blog_system.security;

import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.SignatureAlgorithm;
import io.jsonwebtoken.security.Keys;
import org.springframework.stereotype.Component;

import javax.crypto.SecretKey;
import java.util.Date;

@Component
public class JwtUtil {

    // ✅ 写死：足够长的密钥（≥32 字节）
    private final String SECRET_KEY = "mySuperSecureSecretKeyThatIsLongEnough32Bytes!!";

    // ✅ 写死：24 小时（毫秒）
    private final long EXPIRATION = 86400000; // 24 * 60 * 60 * 1000

    // 将字符串密钥转换为 SecretKey（JJWT 0.10+ 要求）
    private SecretKey getSignInKey() {
        return Keys.hmacShaKeyFor(SECRET_KEY.getBytes());
    }

    public String generateToken(String username) {
        return Jwts.builder()
                .setSubject(username)
                .setIssuedAt(new Date())
                .setExpiration(new Date(System.currentTimeMillis() + EXPIRATION))
                .signWith(getSignInKey(), SignatureAlgorithm.HS256)
                .compact();
    }

    public String extractUsername(String token) {
        return getAllClaimsFromToken(token).getSubject();
    }

    public boolean validateToken(String token, String username) {
        final String extractedUsername = extractUsername(token);
        return (extractedUsername.equals(username) && !isTokenExpired(token));
    }

    private Claims getAllClaimsFromToken(String token) {
        return (Claims) Jwts.parser()
                .verifyWith(getSignInKey())        // ← 不再用 setSigningKey，强调「验证行为」
                .build()
                .parseSignedClaims(token);
    }

    private boolean isTokenExpired(String token) {
        return getAllClaimsFromToken(token).getExpiration().before(new Date());
    }
}