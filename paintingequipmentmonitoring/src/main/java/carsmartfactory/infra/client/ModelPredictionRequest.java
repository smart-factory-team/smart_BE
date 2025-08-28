package carsmartfactory.infra.client;

import lombok.Data;
import lombok.NoArgsConstructor;
import java.time.LocalDateTime;

@Data
@NoArgsConstructor
public class ModelPredictionRequest {
    private String machineId;
    private LocalDateTime timeStamp;
    private Float thick;
    private Float voltage;
    private Float current;
    private Float temper;
    private String issue;
    private Boolean isSolved;
}
