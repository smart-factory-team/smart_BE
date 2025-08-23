package carsmartfactory.infra.client;

import lombok.Data;
import java.time.LocalDateTime;

@Data
public class ModelPredictionResponse {
    private String machineId;
    private LocalDateTime timeStamp;
    private Float thick;
    private Float voltage;
    private Float current;
    private Float temper;
    private String issue; // 모델이 분석한 결과가 여기에 채워짐
    private Boolean isSolved;
}
