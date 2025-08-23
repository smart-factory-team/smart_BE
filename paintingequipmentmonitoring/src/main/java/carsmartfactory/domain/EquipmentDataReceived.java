package carsmartfactory.domain;

import carsmartfactory.infra.AbstractEvent;
import lombok.Data;
import lombok.ToString;

import java.time.LocalDateTime;

/**
 * 시뮬레이터로부터 장비 데이터를 수신했을 때 발행되는 이벤트
 * Kafka 토픽 A로 발행됨
 */
@Data
@ToString
public class EquipmentDataReceived extends AbstractEvent {

    private String machineId;
    private LocalDateTime timeStamp;
    private Float thick;
    private Float voltage;
    private Float current;
    private Float temper;
    private String issue;
    private Boolean isSolved;

    public EquipmentDataReceived() {
        super();
    }
}