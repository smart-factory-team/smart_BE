package carsmartfactory;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.ApplicationContext;
import org.springframework.context.annotation.Bean;
import org.springframework.web.client.RestTemplate;

@SpringBootApplication
public class PressDefectMonitoringApplication {
    
    public static ApplicationContext applicationContext;
    
    public static void main(String[] args) {
        applicationContext = SpringApplication.run(PressDefectMonitoringApplication.class, args);
    }
    
    @Bean
    public RestTemplate restTemplate() {
        return new RestTemplate();
    }
}