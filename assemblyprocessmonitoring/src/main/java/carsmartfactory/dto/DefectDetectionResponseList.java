package carsmartfactory.dto;

import carsmartfactory.domain.DefectDetectionLog;
import lombok.AllArgsConstructor;
import lombok.Getter;

import java.util.List;

@Getter
@AllArgsConstructor
public class DefectDetectionResponseList {
    private List<DefectDetectionResponse> defectDetectionResponses;

    public static DefectDetectionResponseList of(List<DefectDetectionLog> logs) {
        return new DefectDetectionResponseList(
                logs.stream().map(DefectDetectionResponse::of).toList()
        );
    }
}
