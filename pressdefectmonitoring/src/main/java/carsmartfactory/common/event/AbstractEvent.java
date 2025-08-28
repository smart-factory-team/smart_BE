package carsmartfactory.common.event;

import carsmartfactory.PressDefectMonitoringApplication;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.springframework.beans.BeanUtils;
import org.springframework.cloud.stream.function.StreamBridge;
import org.springframework.messaging.MessageHeaders;
import org.springframework.messaging.support.MessageBuilder;
import org.springframework.transaction.support.TransactionSynchronization;
import org.springframework.transaction.support.TransactionSynchronizationManager;
import org.springframework.util.MimeTypeUtils;

//<<< Clean Arch / Outbound Adaptor
public class AbstractEvent {

    String eventType;
    Long timestamp;

    public AbstractEvent(Object aggregate) {
        this();
        BeanUtils.copyProperties(aggregate, this);
    }

    public AbstractEvent() {
        this.setEventType(this.getClass().getSimpleName());
        this.timestamp = System.currentTimeMillis();
    }

    // 기본 publish (기존 토픽)
    public void publish() {
        publishToTopic("eventOut-out-0");
    }
    
    // 원시 데이터 토픽으로 발행
    public void publishToRawDataTopic() {
        publishToTopic("rawDataOut-out-0");
    }
    
    // 결함 탐지 결과 토픽으로 발행
    public void publishToDefectDataTopic() {
        publishToTopic("defectDataOut-out-0");
    }
    
    // 공통 발행 메소드
    private void publishToTopic(String topicBinding) {
        try {
            StreamBridge streamBridge = PressDefectMonitoringApplication.applicationContext.getBean(
                    StreamBridge.class
            );

            streamBridge.send(
                    topicBinding,
                    MessageBuilder
                            .withPayload(this)
                            .setHeader(
                                    MessageHeaders.CONTENT_TYPE,
                                    MimeTypeUtils.APPLICATION_JSON
                            )
                            .setHeader("type", getEventType())
                            .build()
            );
        } catch (Exception e) {
            System.err.println("##### Error publishing event to " + topicBinding + ": " + e.getMessage() + " #####");
            e.printStackTrace();
        }
    }

    public void publishAfterCommit() {
        TransactionSynchronizationManager.registerSynchronization(
                new TransactionSynchronization() {
                    @Override
                    public void afterCommit() {
                        AbstractEvent.this.publish();
                    }
                }
        );
    }

    public String getEventType() {
        return eventType;
    }

    public void setEventType(String eventType) {
        this.eventType = eventType;
    }

    public Long getTimestamp() {
        return timestamp;
    }

    public void setTimestamp(Long timestamp) {
        this.timestamp = timestamp;
    }

    public boolean validate() {
        return getEventType().equals(getClass().getSimpleName());
    }

    public String toJson() {
        ObjectMapper objectMapper = new ObjectMapper();
        String json = null;

        try {
            json = objectMapper.writeValueAsString(this);
        } catch (JsonProcessingException e) {
            throw new RuntimeException("JSON format exception", e);
        }

        return json;
    }
}
//>>> Clean Arch / Outbound Adaptor