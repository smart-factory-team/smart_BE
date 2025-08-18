package carsmartfactory.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.annotation.web.reactive.EnableWebFluxSecurity;
import org.springframework.security.config.web.server.ServerHttpSecurity;
import org.springframework.security.web.server.SecurityWebFilterChain;

@Configuration
@EnableWebFluxSecurity
public class SecurityConfig {

    @Bean
    public SecurityWebFilterChain securityWebFilterChain(ServerHttpSecurity http) {
        return http
                .authorizeExchange(exchanges -> exchanges
                        .anyExchange().permitAll()  // 개발용: 모든 요청 허용
                )
                .csrf(csrf -> csrf.disable())           // CSRF 비활성화
                .cors(cors -> cors.disable())           // CORS 완전 비활성화
                .httpBasic(basic -> basic.disable())    // HTTP Basic 인증 비활성화
                .formLogin(form -> form.disable())      // 폼 로그인 비활성화
                .build();
    }
}