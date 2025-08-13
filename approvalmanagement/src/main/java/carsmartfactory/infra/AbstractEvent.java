package carsmartfactory.infra;

import carsmartfactory.ApprovalmanagementApplication;
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

    public void publish() {
        /**
         * Spring Cloud Stream 4.x 방식 - StreamBridge 사용
         */
        try {
            StreamBridge streamBridge = ApprovalmanagementApplication.applicationContext.getBean(
                    StreamBridge.class
            );

            streamBridge.send(
                    "eventOut-out-0", // application.yml의 바인딩명과 일치
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
            System.err.println("##### Error publishing event: " + e.getMessage() + " #####");
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