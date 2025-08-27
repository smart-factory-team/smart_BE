package carsmartfactory.dto;

import carsmartfactory.domain.DefectDetectionLog;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.util.List;

@AllArgsConstructor
@Getter
@NoArgsConstructor
public class MonitoringResponse {
    private List<DefectDetectionResponse> defectDetectionResponses;

    private Long totalProductCount;

    private Long defectProductCount;

    public static MonitoringResponse of(List<DefectDetectionLog> logs, Long totalProductCount, Long defectProductCount) {
        return new MonitoringResponse(
                logs.stream().map(DefectDetectionResponse::of).toList(),
                totalProductCount,
                defectProductCount
        );
    }
}
