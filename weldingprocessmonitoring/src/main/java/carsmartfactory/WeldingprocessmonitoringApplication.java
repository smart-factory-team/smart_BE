package carsmartfactory;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cloud.openfeign.EnableFeignClients;
import org.springframework.context.ApplicationContext;

@SpringBootApplication
@EnableFeignClients
public class WeldingprocessmonitoringApplication {

    public static ApplicationContext applicationContext;

    public static void main(String[] args) {
        applicationContext =
                SpringApplication.run(
                        WeldingprocessmonitoringApplication.class,
                        args
                );
    }
}
