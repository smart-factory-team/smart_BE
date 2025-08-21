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
            // 시뮬레이터가 호출하는 API 경로는 인증 없이 접근할 수 있도록 허용
            .authorizeHttpRequests(authorize -> authorize
                .requestMatchers("/**").permitAll() 
                .anyRequest().authenticated() // 그 외 나머지 모든 요청은 인증을 요구
            )
            // 해당 경로에 대해서는 CSRF 보호 기능 비활성화
            .csrf(csrf -> csrf.ignoringRequestMatchers("/**"));

        return http.build();
    }
}
