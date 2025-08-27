package carsmartfactory.dto;

import carsmartfactory.domain.DefectDetectionLog;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@AllArgsConstructor
@NoArgsConstructor
public class DefectDetectionResponse {
    private String id;
    private Long machineId;
    private String partType;
    private String defectType;
    private String imagePath;

    public static DefectDetectionResponse of(DefectDetectionLog log){
        return new DefectDetectionResponse(
                log.getId(), log.getMachineId(), log.getPart(), log.getCategory(), log.getImageUrl()
        );
    }
}
