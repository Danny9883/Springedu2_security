package com.example.springedu2.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;

// SpringSecurity 의 설정
@Configuration
public class SecutiryConfig {

    @Bean
    public SecurityFilterChain securityFilterChain(HttpSecurity http) throws Exception {
        http.csrf(csrf -> csrf.disable())  // 실무는 설정, 공부 설정안함
                .authorizeHttpRequests(auth -> auth
                        .requestMatchers(
                                "/", "/index.html",
                                "/css/**", "/img/**", "/js/**","/fonts/**",
                                "/login", "/members/register"
                        ).permitAll()  // 로그인 없이 사용 가능
                        .requestMatchers("/admin/**","/vupdate", "/vdelete").hasRole("ADMIN")
                        .requestMatchers(
                                "/visitorMain.html", "/visitorForm.html",
                                "/vlist", "/vinsert", "/vsearch", "/one", "/members/me"
                        ).authenticated() // 로그인이 필요하다
                        .anyRequest().authenticated() // 설정하지 않은 다른 요청도 로그인 필요
                ).formLogin(form -> form.loginPage("/login"))
                .logout(logout -> logout.logoutUrl("/logout"))
                .exceptionHandling(exception ->
                        exception.accessDeniedPage("/access-denied")
                ); // 접근 거부 페이지 처리

        return http.build();

    }

    // 비밀번호를 암호화
    @Bean
    public BCryptPasswordEncoder bCryptPasswordEncoder() {
        return new BCryptPasswordEncoder();
    }



}
