package com.advisor.util;

import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.SignatureAlgorithm;
import io.jsonwebtoken.security.Keys;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import java.nio.charset.StandardCharsets;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Component
public class JwtUtil {

    @Value("${jwt.secret}")
    private String jwtSecret;

    @Value("${jwt.expiration}")
    private long jwtExpiration;

    /**
     * 生成 JWT Token
     *
     * @param userId   用户ID
     * @param username 用户名
     * @param roles    用户角色列表
     * @return 生成的 Token 字符串
     */
    public String generateToken(String userId, String username, List<String> roles) {
        Date now = new Date();
        Date expiryDate = new Date(now.getTime() + jwtExpiration);

        Map<String, Object> claims = new HashMap<>();
        claims.put("username", username);
        claims.put("roles", roles); // 将角色列表添加到 claims

        return Jwts.builder()
                .setClaims(claims) // 使用包含角色的 claims
                .setSubject(userId) // subject 仍然是 userId
                .setIssuedAt(now)
                .setExpiration(expiryDate)
                .signWith(Keys.hmacShaKeyFor(jwtSecret.getBytes(StandardCharsets.UTF_8)), SignatureAlgorithm.HS512)
                .compact();
    }

    /**
     * 从 Token 中解析 Claims
     *
     * @param token JWT Token
     * @return Claims 对象
     */
    public Claims getClaimsFromToken(String token) {
         // 移除 "Bearer " 前缀（如果存在）
        String actualToken = token.startsWith("Bearer ") ? token.substring(7) : token;
        try {
             return Jwts.parserBuilder()
                    .setSigningKey(Keys.hmacShaKeyFor(jwtSecret.getBytes(StandardCharsets.UTF_8)))
                    .build()
                    .parseClaimsJws(actualToken)
                    .getBody();
        } catch (Exception e) {
            // 可以根据需要处理不同的异常，例如 TokenExpiredException, SignatureException 等
            System.err.println("解析Token失败: " + e.getMessage());
            // 在实际应用中可能需要向上抛出自定义异常或返回 null/Optional.empty()
            // 这里为了简化，暂时返回 null，调用方需要处理 null 情况
            return null;
        }
    }

    /**
     * 从 Token 中获取用户ID
     *
     * @param token JWT Token
     * @return 用户ID
     */
    public String getUserIdFromToken(String token) {
        Claims claims = getClaimsFromToken(token);
        return claims != null ? claims.getSubject() : null;
    }

    /**
     * 从 Token 中获取用户名
     *
     * @param token JWT Token
     * @return 用户名
     */
    public String getUsernameFromToken(String token) {
        Claims claims = getClaimsFromToken(token);
        return claims != null ? claims.get("username", String.class) : null;
    }

     /**
     * 从 Token 中获取角色列表
     *
     * @param token JWT Token
     * @return 角色列表
     */
    @SuppressWarnings("unchecked") // Jwts 返回的是 Object，需要强制转换
    public List<String> getRolesFromToken(String token) {
        Claims claims = getClaimsFromToken(token);
        return claims != null ? claims.get("roles", List.class) : null;
    }


    /**
     * 验证 Token 是否有效（未过期且签名正确）
     *
     * @param token JWT Token
     * @return 是否有效
     */
    public boolean validateToken(String token) {
        try {
             // 尝试解析 Token，如果成功且未抛出异常，则表示签名有效且未过期
            getClaimsFromToken(token);
            return true;
        } catch (Exception e) {
            return false;
        }
    }
} 