package com.example.datn.config;

import com.example.datn.security.CustomUserDetailsService;
import com.example.datn.security.CustomSuccessHandler;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.authentication.dao.DaoAuthenticationProvider;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.crypto.password.NoOpPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;

@Configuration
public class SecurityConfig {

    @Autowired
    private CustomUserDetailsService customUserDetailsService;

    @Autowired
    private CustomSuccessHandler customSuccessHandler;

    @Bean
    public SecurityFilterChain filterChain(HttpSecurity http) throws Exception {
        http
                .authorizeHttpRequests(auth -> auth
                        // Cho phép truy cập public
                        .requestMatchers(
                                "/", "/home",
                                "/login", "/dangky", "/dangki",
                                "/product/**",
                                "/collections/**",
                                "/api/cart/**",
                                "/css/**", "/js/**", "/**",
                                "/*.jpg", "/*.png", "/*.gif"
                        ).permitAll()
                        // Chỉ ADMIN mới truy cập được
                        .requestMatchers(
                                "/admin/**",
                                "/quanlynguoidung",
                                "/quanlysanpham",
                                "/quanlyhoadon",
                                "/quanlydanhmuc",
                                "/quanlythuonghieu",
                                "/quanlybanhang",
                                "/quanlymagiam"
                        ).hasRole("ADMIN")
                        // Các request khác phải đăng nhập
                        .anyRequest().authenticated()
                )
                .formLogin(form -> form
                        .loginPage("/login")
                        .loginProcessingUrl("/login")
                        .usernameParameter("taiKhoan")
                        .passwordParameter("matKhau")
                        .successHandler(customSuccessHandler)
                        .failureUrl("/login?error")
                        .permitAll()
                )
                .logout(logout -> logout
                        .logoutUrl("/logout")
                        .logoutSuccessUrl("/login?logout")
                        .permitAll()
                )
                .csrf(csrf -> csrf.disable());

        return http.build();
    }

    @Bean
    public PasswordEncoder passwordEncoder() {
        return NoOpPasswordEncoder.getInstance();
    }

    @Bean
    public DaoAuthenticationProvider authenticationProvider() {
        DaoAuthenticationProvider provider = new DaoAuthenticationProvider();
        provider.setUserDetailsService(customUserDetailsService);
        provider.setPasswordEncoder(passwordEncoder());
        return provider;
    }
}