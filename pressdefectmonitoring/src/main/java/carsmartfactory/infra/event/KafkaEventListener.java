package carsmartfactory.infra.event;

import carsmartfactory.domain.event.PressDefectDataReceivedEvent;
import carsmartfactory.domain.event.PressDefectResultDetectedEvent;
import lombok.extern.slf4j.Slf4j;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import java.util.function.Supplier;

@Slf4j
@Configuration
public class KafkaEventListener {

    /**
     * 원시 데이터 이벤트 발행용 Supplier (실제로는 호출되지 않음)
     */
    @Bean
    public Supplier<PressDefectDataReceivedEvent> rawDataOut() {
        return () -> null; // AbstractEvent에서 직접 발행하므로 실제로는 사용되지 않음
    }

    /**
     * 결함 탐지 결과 이벤트 발행용 Supplier (실제로는 호출되지 않음)
     */
    @Bean
    public Supplier<PressDefectResultDetectedEvent> defectDataOut() {
        return () -> null; // AbstractEvent에서 직접 발행하므로 실제로는 사용되지 않음
    }
}