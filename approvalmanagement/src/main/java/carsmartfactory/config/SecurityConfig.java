package carsmartfactory.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.HttpMethod;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.config.annotation.web.configurers.AbstractHttpConfigurer;

@Configuration
@EnableWebSecurity
public class SecurityConfig {

    @Bean
    public SecurityFilterChain filterChain(HttpSecurity http) throws Exception {
        http
                .csrf(AbstractHttpConfigurer::disable)
                .sessionManagement(sm -> sm.sessionCreationPolicy(SessionCreationPolicy.STATELESS))
                .authorizeHttpRequests(auth -> auth
                        // Swagger & 문서
                        .requestMatchers(
                                "/v3/api-docs/**",
                                "/swagger-ui/**",
                                "/swagger-ui.html",
                                "/swagger-resources/**",
                                "/webjars/**",
                                "/",
                                "/error",
                                "/actuator/**"
                        ).permitAll()

                        // 조회는 공개 (원하면 여기도 ADMIN으로 바꿔도 됨)
                        .requestMatchers(HttpMethod.GET, "/approvals/**").permitAll()

                        // 승인/거절은 ADMIN만 (컨트롤러가 POST 매핑)
                        .requestMatchers(HttpMethod.POST, "/approvals/*/approve", "/approvals/*/reject")
                        .hasRole("ADMIN")
                        // 혹시 나중에 /api 프리픽스 쓸 수도 있으니 같이 허용
                        .requestMatchers(HttpMethod.POST, "/api/approvals/*/approve", "/api/approvals/*/reject")
                        .hasRole("ADMIN")

                        // 그 외는 전부 허용 (개발 편의)
                        .anyRequest().permitAll()
                )
                // 로그인 화면/Basic 인증 비활성화 → Swagger가 절대 로그인으로 튕기지 않음
                .formLogin(AbstractHttpConfigurer::disable)
                .httpBasic(AbstractHttpConfigurer::disable)
                .logout(AbstractHttpConfigurer::disable);

        return http.build();
    }
}
