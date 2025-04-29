package com.advisor.config;

import com.advisor.util.JwtUtil; // 导入 JwtUtil
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Component;
import org.springframework.util.StringUtils;
import org.springframework.web.filter.OncePerRequestFilter;

import javax.servlet.FilterChain;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.util.Collections;
import java.util.List;
import java.util.stream.Collectors;

@Component
public class JwtAuthenticationFilter extends OncePerRequestFilter {

    @Autowired
    private JwtUtil jwtUtil; // 注入 JwtUtil

    @Override
    protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response, FilterChain filterChain)
            throws ServletException, IOException {

        String token = resolveToken(request);

        if (StringUtils.hasText(token) && jwtUtil.validateToken(token)) {
            try {
                String username = jwtUtil.getUsernameFromToken(token);
                List<String> roles = jwtUtil.getRolesFromToken(token);

                if (username != null && roles != null && !roles.isEmpty()) {
                    // 将角色字符串列表转换为 GrantedAuthority 列表
                    List<GrantedAuthority> authorities = roles.stream()
                            .map(SimpleGrantedAuthority::new)
                            .collect(Collectors.toList());

                    // 创建认证对象
                    UsernamePasswordAuthenticationToken authentication = new UsernamePasswordAuthenticationToken(
                            username, // Principal 可以是用户名，也可以是 UserDetails 对象
                            null,     // Credentials (密码) 在 JWT 认证中通常为 null
                            authorities // 用户的权限/角色
                    );

                    // 设置认证信息到 SecurityContextHolder
                    SecurityContextHolder.getContext().setAuthentication(authentication);
                    logger.debug("用户 '" + username + "' 已认证，角色: " + roles);
                } else {
                    logger.warn("Token有效，但无法从中获取用户名或角色: " + token);
                }
            } catch (Exception e) {
                // 处理从 Token 获取信息时的潜在异常 (虽然 validateToken 应该已经捕获了大部分)
                logger.error("处理已验证的Token时出错: " + token, e);
                SecurityContextHolder.clearContext(); // 确保清除上下文
            }
        } else {
             if (StringUtils.hasText(token)) {
                 logger.debug("无效的JWT Token: " + token);
             } else {
                 // logger.trace("请求没有JWT Token"); // 可以根据需要取消注释
             }
             SecurityContextHolder.clearContext(); // 确保清除上下文
        }

        // 继续过滤器链
        filterChain.doFilter(request, response);
    }

    /**
     * 从 HttpServletRequest 中提取 Bearer Token
     */
    private String resolveToken(HttpServletRequest request) {
        String bearerToken = request.getHeader("Authorization");
        if (StringUtils.hasText(bearerToken) && bearerToken.startsWith("Bearer ")) {
            return bearerToken.substring(7);
        }
        return null;
    }

}