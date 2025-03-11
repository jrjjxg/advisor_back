package com.advisor.config;

import com.advisor.entity.AdminUser;
import com.advisor.service.AdminUserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Lazy;
import org.springframework.security.config.annotation.authentication.builders.AuthenticationManagerBuilder;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configuration.WebSecurityConfigurerAdapter;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.core.authority.AuthorityUtils;
import org.springframework.security.core.userdetails.User;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.http.HttpMethod;

@Configuration
@EnableWebSecurity
public class SecurityConfig extends WebSecurityConfigurerAdapter {

    @Autowired
    @Lazy
    private AdminUserService adminUserService;

    @Bean
    public PasswordEncoder passwordEncoder() {
        return new BCryptPasswordEncoder();
    }

    @Override
    protected void configure(AuthenticationManagerBuilder auth) throws Exception {
        auth.userDetailsService(username -> {
            AdminUser user = adminUserService.getCurrentUser();
            if (user == null) {
                throw new UsernameNotFoundException("用户不存在");
            }
            return new User(user.getUsername(), user.getPassword(), AuthorityUtils.createAuthorityList("ROLE_ADMIN"));
        }).passwordEncoder(passwordEncoder());
    }

    @Override
    protected void configure(HttpSecurity http) throws Exception {
        http
            .cors() // 启用 CORS
            .and()
            .csrf().disable() // 禁用CSRF保护
            .sessionManagement()
            .sessionCreationPolicy(SessionCreationPolicy.STATELESS)
            .and()
            .authorizeRequests()
                .antMatchers(HttpMethod.OPTIONS, "/**").permitAll() // 允许所有 OPTIONS 请求
                .antMatchers("/api/admin/register/**", "/api/admin/login").permitAll() // 后台管理的注册和登录
                .antMatchers("/api/tests/**").permitAll() // 允许所有 /api/tests/** 路径 (移动端)
                .antMatchers("/api/profile").permitAll() // 允许/api/profile路径
                .antMatchers("/api/moods/**").permitAll()  // 允许情绪记录相关接口
                .anyRequest().authenticated() // 其他请求需要认证
            .and()
            .formLogin().disable() // 禁用默认的表单登录
            .httpBasic().disable() // 禁用HTTP Basic认证
            .sessionManagement()
                .sessionCreationPolicy(SessionCreationPolicy.IF_REQUIRED); // 需要时创建Session
    }
}