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
        System.out.println("##### Attempting to publish event: " + getEventType() + " #####");

        try {
            StreamBridge streamBridge = ApprovalmanagementApplication.applicationContext.getBean(
                    StreamBridge.class
            );

            boolean sent = streamBridge.send(
                    "eventOut-out-0",
                    MessageBuilder
                            .withPayload(this)
                            .setHeader(
                                    MessageHeaders.CONTENT_TYPE,
                                    MimeTypeUtils.APPLICATION_JSON
                            )
                            .setHeader("type", getEventType())
                            .build()
            );

            if (sent) {
                System.out.println("##### Event published successfully to Kafka: " + getEventType() + " #####");
                System.out.println("##### Event payload: " + this.toJson() + " #####");
            } else {
                System.err.println("##### Failed to publish event to Kafka: " + getEventType() + " #####");
                throw new RuntimeException("Failed to publish event to Kafka: " + getEventType());
            }

        } catch (Exception e) {
            System.err.println("##### Error publishing event: " + getEventType() + " - " + e.getMessage() + " #####");
            throw new RuntimeException("Failed to publish event: " + getEventType(), e);
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