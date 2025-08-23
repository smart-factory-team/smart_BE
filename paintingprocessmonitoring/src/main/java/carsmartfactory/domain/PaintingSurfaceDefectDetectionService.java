package carsmartfactory.domain;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.JsonNode;

import carsmartfactory.infra.PaintingSurfaceModelClient;
import carsmartfactory.infra.dto.PaintingSurfacePredictionResponseDto;

import java.util.Date;
import java.util.Map;

@Service
public class PaintingSurfaceDefectDetectionService {
    
    @Autowired
    private PaintingSurfaceModelClient modelClient;
    
    @Autowired
    private ObjectMapper objectMapper;
    
    /**
     * 시뮬레이터로부터 받은 이미지로 결함 감지 수행
     * @param image 업로드된 이미지 파일
     * @return 결함 감지 결과
     */
    public DefectDetectionResult detectDefect(MultipartFile image) {
        try {
            System.out.println("🔍 결함 감지 시작: " + image.getOriginalFilename());
            
            // 1단계: AI 모델 서비스 호출 (모델 클라이언트 사용)
            PaintingSurfacePredictionResponseDto prediction = modelClient.predict(image, 0.5f);
            
            if (prediction != null) {
                System.out.println("✅ AI 모델 응답 받음");
                
                // 2단계: AI 모델 응답을 DefectDetectionResult로 변환
                DefectDetectionResult result = convertAIResponseToDefectResult(prediction);
                
                // 3단계: 결함이 감지된 경우에만 DB에 저장
                if ("defect".equals(result.getStatus())) {
                    System.out.println("🚨 결함 감지됨 - DB에 저장 시작");
                    saveDefectDetectionLog(image, result);
                    System.out.println("💾 결함 감지 로그 DB 저장 완료");
                } else {
                    System.out.println("✅ 정상 상태 - DB 저장 없음");
                }
                
                return result;
            } else {
                System.out.println("❌ AI 모델 서비스 응답 없음");
                throw new RuntimeException("AI 모델 서비스 응답 없음");
            }
            
        } catch (Exception e) {
            System.err.println("❌ 결함 감지 처리 중 오류: " + e.getMessage());
            throw new RuntimeException("결함 감지 처리 중 오류: " + e.getMessage());
        }
    }
    
    /**
     * AI 모델의 PredictionResponse를 DefectDetectionResult로 변환
     * @param prediction AI 모델의 예측 응답
     * @return 변환된 DefectDetectionResult
     */
    private DefectDetectionResult convertAIResponseToDefectResult(PaintingSurfacePredictionResponseDto prediction) {
        try {
            // predictions 배열 확인
            if (prediction.getPredictions() == null || prediction.getPredictions().isEmpty()) {
                // 결함이 감지되지 않은 경우
                return new DefectDetectionResult("normal", null, 1.0, "정상 상태 - 결함 없음");
            }
            
            // 첫 번째 결함 정보 추출
            Map<String, Object> firstPrediction = prediction.getPredictions().get(0);
            if (firstPrediction != null) {
                String defectType = (String) firstPrediction.get("class_name");
                Double confidence = (Double) firstPrediction.get("confidence");
                
                if (defectType != null && confidence != null) {
                    return new DefectDetectionResult(
                        "defect",           // status: 결함 감지
                        defectType,         // defectType: 결함 유형
                        confidence,         // confidence: 신뢰도
                        "결함 감지됨: " + defectType  // message: 결함 정보
                    );
                }
            }
            
            // 기본값: 정상 상태
            return new DefectDetectionResult("normal", null, 1.0, "정상 상태");
            
        } catch (Exception e) {
            System.err.println("❌ AI 응답 변환 중 오류: " + e.getMessage());
            // 변환 실패 시 기본값 반환
            return new DefectDetectionResult("error", null, 0.0, "AI 응답 변환 실패: " + e.getMessage());
        }
    }
    
    /**
     * 결함 감지 로그를 DB에 저장
     * @param image 원본 이미지 파일
     * @param result AI 모델의 감지 결과
     */
    private void saveDefectDetectionLog(MultipartFile image, DefectDetectionResult result) {
        try {
            // 새로운 결함 감지 로그 엔티티 생성
            PaintingSurfaceDefectDetectionLog log = new PaintingSurfaceDefectDetectionLog();
            
            // 기본 정보 설정
            log.setMachineName("Painting-Surface-Detector");
            log.setItemNo(image.getOriginalFilename());
            log.setTimeStamp(new Date());
            log.setDefectType(result.getDefectType());
            log.setIssue("Surface defect detected");
            log.setIsSolved(false);
            
            // AI 모델 결과 정보 설정
            if (result.getConfidence() != null) {
                log.setPressTime(result.getConfidence().doubleValue());
            }
            
            // DB에 저장 (기존 repository() 메서드 사용)
            PaintingSurfaceDefectDetectionLog.repository().save(log);
            
            System.out.println("💾 로그 저장 완료: " + log.getId());
            
        } catch (Exception e) {
            System.err.println("❌ 로그 저장 중 오류: " + e.getMessage());
            throw new RuntimeException("로그 저장 중 오류: " + e.getMessage());
        }
    }
}
