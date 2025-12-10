package com.wangjun.text_proof_platform.config;


import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;

@Configuration
@EnableWebSecurity
public class SecurityConfig {

    @Bean
    public SecurityFilterChain filterChain(HttpSecurity http) throws Exception {
        http
                .csrf(csrf -> csrf.disable()) // 关闭 CSRF 防护 (对于新手开发 API 比较方便，生产环境需注意)
                .authorizeHttpRequests(auth -> auth
                        // 关键点：允许 "/api/auth/" 开头的请求随便访问 (注册、登录)
                        // 也允许 "/h2-console/" 随便访问 (数据库控制台)
                        .requestMatchers("/api/auth/**").permitAll()
                        // 其他所有请求，必须登录后才能访问
                        .anyRequest().authenticated()
                )
                .headers(headers -> headers.frameOptions(frame -> frame.disable())); // 为了让 H2 控制台能正常显示
        return http.build();
    }

    // 这是一个密码加密器。它会把 "123456" 变成 "$2a$10$xd..." 这种乱码。
    @Bean
    public PasswordEncoder passwordEncoder() {
        return new BCryptPasswordEncoder();
    }
}