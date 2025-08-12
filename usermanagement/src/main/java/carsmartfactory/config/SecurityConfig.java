package carsmartfactory.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.web.SecurityFilterChain;

@Configuration
@EnableWebSecurity
public class SecurityConfig {

    @Bean
    public SecurityFilterChain filterChain(HttpSecurity http) throws Exception {
        http
                // 개발용: 모든 요청 허용
                .authorizeHttpRequests(authz -> authz
                        .anyRequest().permitAll()  // 모든 요청 허용
                )
                // CSRF 완전 비활성화
                .csrf(csrf -> csrf.disable())
                // 기본 인증 비활성화
                .httpBasic(basic -> basic.disable())
                // 폼 로그인 비활성화
                .formLogin(form -> form.disable())
                // 헤더 설정
                .headers(headers -> headers
                        .frameOptions(frameOptions -> frameOptions.disable())
                );

        return http.build();
    }
}