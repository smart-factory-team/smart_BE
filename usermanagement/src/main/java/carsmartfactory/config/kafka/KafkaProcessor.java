package carsmartfactory.config.kafka;

// import org.springframework.cloud.stream.annotation.Input;  // 제거
// import org.springframework.cloud.stream.annotation.Output; // 제거

import org.springframework.messaging.MessageChannel;
import org.springframework.messaging.SubscribableChannel;

/**
 * Kafka 프로세서 인터페이스 Spring Cloud Stream 4.x에서는 @Input/@Output이 제거되었지만 기본 구조는 유지하고 application.yml 설정으로 대체
 */
public interface KafkaProcessor {
    String INPUT = "event-in";
    String OUTPUT = "event-out";

    // @Input(INPUT)  // 어노테이션만 제거, 메서드는 유지
    SubscribableChannel inboundTopic();

    // @Output(OUTPUT) // 어노테이션만 제거, 메서드는 유지
    MessageChannel outboundTopic();
}