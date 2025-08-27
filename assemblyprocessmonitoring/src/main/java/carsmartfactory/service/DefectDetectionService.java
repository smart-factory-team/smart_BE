package carsmartfactory.service;

import carsmartfactory.domain.DefectDetectionLog;
import carsmartfactory.domain.DefectDetectionLogRepository;
import carsmartfactory.dto.DefectDetectionResponseList;
import carsmartfactory.dto.MonitoringResponse;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.AllArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.util.Date;
import java.util.List;

@Service
@AllArgsConstructor
public class DefectDetectionService {

    private DefectDetectionLogRepository defectDetectionLogRepository;

    public void saveResult(String aiResponse, MultipartFile imageFile, Long machineId) {
        try {
            ObjectMapper objectMapper = new ObjectMapper();
            JsonNode responseJson = objectMapper.readTree(aiResponse);
            JsonNode data = responseJson.get("data");
            JsonNode fileInfo = data.get("file_info");

            DefectDetectionLog defectDetectionLog = new DefectDetectionLog();
            defectDetectionLog.setMachineId(machineId);
            defectDetectionLog.setTimeStamp(new Date());

            String predictedLabel = data.get("predicted_label").asText();
            boolean isDefective = data.get("is_defective").asBoolean();

            defectDetectionLog.setWork("조립");

            String filename = fileInfo.get("filename").asText();
            String partCode = filename.substring(0,3);
            defectDetectionLog.setPart(getPartName(partCode));

            String[] issues = predictedLabel.split("_");
            defectDetectionLog.setIssue(issues[0]);
            defectDetectionLog.setCategory(issues[1]);

            defectDetectionLog.setImageName(filename);
            defectDetectionLog.setImageUrl("https://simulatorstorage.blob.core.windows.net/simulator-data/assembly-1/" + filename);
            defectDetectionLog.setImageWidth(fileInfo.get("width").asLong());
            defectDetectionLog.setImageHeight(fileInfo.get("height").asLong());

            defectDetectionLog.setIsSolved(false);

            defectDetectionLogRepository.save(defectDetectionLog);
        }catch (Exception e) {
            e.printStackTrace();
        }
    }

    private String getPartName(String partCode) {
        return switch (partCode) {
            case "204" -> "도어";
            case "208" -> "라디에이터 그릴";
            case "206" -> "루프사이드";
            case "202" -> "배선";
            case "205" -> "범퍼";
            case "203" -> "카울커버";
            case "201" -> "커넥터";
            case "210" -> "테일 램프";
            case "207" -> "프레임";
            case "211" -> "헤드 램프";
            case "209" -> "휀더";
            default -> "알 수 없음";
        };
    }

    public DefectDetectionResponseList getDefectDetectionsByMachineId(Long machineId) {
        return DefectDetectionResponseList.of(
                defectDetectionLogRepository.findByMachineIdOrderByTimeStampDesc(machineId)
        );
    }

    public MonitoringResponse getDefectDetectionsByMachineId(Long machineId, Long productCount) {

        List<DefectDetectionLog> allData = defectDetectionLogRepository.findByMachineIdOrderByTimeStampDesc(machineId);

        long totalCount = allData.size();

        List<DefectDetectionLog> newData = allData.stream()
                .limit(productCount == null || productCount <= 0 ? totalCount : totalCount - productCount)
                .toList();

        return MonitoringResponse.of(newData, totalCount, countDefectProductByCategoryAndMachineId(machineId));
    }

    private long countDefectProductByCategoryAndMachineId(Long machineId) {
        return defectDetectionLogRepository.countByCategoryAndMachineId("불량품", machineId);
    }
}
