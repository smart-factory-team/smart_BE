package carsmartfactory.infra.websocket;

import carsmartfactory.application.controller.response.PressDefectResultResponseDto;
import carsmartfactory.domain.repository.PressDefectDetectionLogRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import java.time.Instant;
import java.util.HashMap;
import java.util.Map;

@Slf4j
@Service
@RequiredArgsConstructor
public class WebSocketService {

    private final MonitoringWebSocketHandler webSocketHandler;
    private final PressDefectDetectionLogRepository defectLogRepository;

    // 인자를 3개 받도록 시그니처 수정
    public void sendMonitoringData(PressDefectResultResponseDto result, long totalProducts, long totalDefects) {
        try {
            log.info("모니터링 데이터 전송 시작: {}",
                    result.getFinalJudgment().getInspectionId());

            // 인자를 3개 받도록 createMonitoringData 메서드 호출 수정
            Map<String, Object> monitoringData = createMonitoringData(result, totalProducts, totalDefects);
            webSocketHandler.broadcastMonitoringData(monitoringData);

            log.info("모니터링 데이터 전송 완료: {}",
                    result.getFinalJudgment().getInspectionId());

        } catch (Exception e) {
            log.error("모니터링 데이터 전송 실패: {} - {}",
                    result.getFinalJudgment().getInspectionId(), e.getMessage(), e);
        }
    }

    // 인자를 3개 받도록 시그니처 수정
    private Map<String, Object> createMonitoringData(PressDefectResultResponseDto result, long totalProducts, long totalDefects) {
        Map<String, Object> data = new HashMap<>();

        // 기본 정보
        data.put("type", "PRESS_DEFECT_MONITORING");
        data.put("timestamp", Instant.now().toString());
        data.put("inspectionId", result.getFinalJudgment().getInspectionId());

        // 검사 결과
        data.put("qualityStatus", result.getFinalJudgment().getQualityStatus());
        data.put("recommendation", result.getFinalJudgment().getRecommendation());
        data.put("isDefective", result.isDefective());
        data.put("isComplete", result.getFinalJudgment().getIsComplete());
        data.put("missingHoles", result.getFinalJudgment().getMissingHoles());

        // 처리 정보
        if (result.getInspectionInfo() != null) {
            data.put("expectedImages", result.getInspectionInfo().getExpectedImages());
            data.put("actualImages", result.getInspectionInfo().getActualImages());
            data.put("isCompleteDataset", result.getInspectionInfo().getIsCompleteDataset());
        }

        // 품질 검사 상세 정보
        if (result.getQualityInspection() != null) {
            data.put("processedImages", result.getQualityInspection().getProcessedImages());
            data.put("existingCategories", result.getQualityInspection().getExistingCategories());
            data.put("missingCategories", result.getQualityInspection().getMissingCategories());
            data.put("missingCategoryNames", result.getQualityInspection().getMissingCategoryNames());
        }

        // 알림 레벨 설정
        if (result.isDefective()) {
            data.put("alertLevel", "ERROR");
            data.put("alertMessage", "결함품이 감지되었습니다");
        } else {
            data.put("alertLevel", "SUCCESS");
            data.put("alertMessage", "정상품으로 판정되었습니다");
        }

        // 누적 통계 필드는 인자로 받은 값을 사용
        data.put("totalProducts", totalProducts);
        data.put("totalDefects", totalDefects);

        return data;
    }

    // 더 이상 필요 없는 메서드들을 제거
    // private long getTotalProductsFromDatabase() {
    //      return defectLogRepository.count();
    // }
    //
    // private long getTotalDefectsFromDatabase() {
    //      return defectLogRepository.countDefective();
    // }
}