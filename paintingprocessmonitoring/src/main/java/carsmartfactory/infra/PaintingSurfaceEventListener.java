package carsmartfactory.infra;

import carsmartfactory.domain.*;
import carsmartfactory.infra.dto.PaintingSurfacePredictionResponseDto;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.event.EventListener;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;

import java.util.Date;
import java.util.List;
import java.util.Map;

@Service
public class PaintingSurfaceEventListener {

    @Autowired
    private PaintingSurfaceModelClient modelClient;

    @Autowired
    private PaintingSurfaceDefectDetectionLogRepository repository;



    @EventListener
    @Transactional
    public void handleImageReceived(ImageReceivedEvent event) {
        
        System.out.println("=== 1단계: 도장표면 이미지 수신 이벤트 처리 ===");
        System.out.println("이미지명: " + event.getImage().getOriginalFilename());
        System.out.println("이미지 크기: " + event.getImage().getSize() + " bytes");
        
        try {
            // 1단계: AI 모델 서비스 호출
            PaintingSurfacePredictionResponseDto prediction = modelClient.predict(
                event.getImage(), 
                0.5f  // 기본 신뢰도 임계값
            );
            
            if (prediction != null) {
                System.out.println("=== AI 모델 응답 성공 ===");
                
                if (prediction.getPredictions() != null && !prediction.getPredictions().isEmpty()) {
                    System.out.println("=== 결함 감지: " + prediction.getPredictions().size() + "개 결함 발견 ===");
                    
                    // 모든 결함을 개별 레코드로 저장
                    for (int i = 0; i < prediction.getPredictions().size(); i++) {
                        Map<String, Object> singlePrediction = prediction.getPredictions().get(i);
                        DefectDetectionResult result = convertSinglePrediction(singlePrediction, i);
                        saveDefectDetectionLog(event.getImage(), result, i);
                    }
                    
                    System.out.println("=== 모든 결함 DB 저장 및 이벤트 발행 완료 ===");
                    
                } else {
                    System.out.println("=== 정상 상태 - DB 저장 없음 ===");
                }
                
            } else {
                System.out.println("=== AI 모델 응답 없음 ===");
            }
            
        } catch (Exception e) {
            System.err.println("=== 이미지 처리 중 오류: " + e.getMessage() + " ===");
            e.printStackTrace();
        }
    }

    /**
     * AI 모델 예측 결과를 DefectDetectionResult로 변환
     */
    private DefectDetectionResult convertPredictionToDefectResult(PaintingSurfacePredictionResponseDto prediction) {
        try {
            if (prediction.getPredictions() == null || prediction.getPredictions().isEmpty()) {
                return new DefectDetectionResult("normal", null, 1.0, "정상 상태 - 결함 없음");
            }
            
            // 첫 번째 결함 정보 추출
            Map<String, Object> firstPrediction = prediction.getPredictions().get(0);
            if (firstPrediction != null) {
                String defectType = (String) firstPrediction.get("class_name");
                Double confidence = (Double) firstPrediction.get("confidence");
                
                // 결함 위치 및 크기 정보 추출
                Float defectX = extractFloatValue(firstPrediction, "center_x");
                Float defectY = extractFloatValue(firstPrediction, "center_y");
                Float defectWidth = extractFloatValue(firstPrediction, "width");
                Float defectHeight = extractFloatValue(firstPrediction, "height");
                Float defectArea = extractFloatValue(firstPrediction, "area");
                String defectBbox = extractBboxString(firstPrediction);
                
                return new DefectDetectionResult(
                    "defect",           // status: 결함 감지
                    defectType,         // defectType: 결함 유형
                    confidence,         // confidence: 신뢰도
                    "결함 감지됨: " + defectType,  // message: 결함 정보
                    defectX, defectY, defectWidth, defectHeight, defectArea, defectBbox  // 위치/크기 정보
                );
            }
            
            return new DefectDetectionResult("normal", null, 1.0, "정상 상태");
            
        } catch (Exception e) {
            System.err.println("=== 예측 결과 변환 중 오류: " + e.getMessage() + " ===");
            return new DefectDetectionResult("error", null, 0.0, "예측 결과 변환 실패: " + e.getMessage());
        }
    }
    
    /**
     * 개별 결함 예측 결과를 DefectDetectionResult로 변환
     */
    private DefectDetectionResult convertSinglePrediction(Map<String, Object> prediction, int index) {
        try {
            String defectType = (String) prediction.get("class_name");
            Double confidence = (Double) prediction.get("confidence");
            
            // bbox에서 위치 및 크기 정보 계산
            Float defectX = null;
            Float defectY = null;
            Float defectWidth = null;
            Float defectHeight = null;
            Float defectArea = extractFloatValue(prediction, "area");
            String defectBbox = extractBboxString(prediction);
            
            // bbox가 있으면 center_x, center_y, width, height 계산
            if (defectBbox != null) {
                try {
                    // bbox = "[x1, y1, x2, y2]" 형태의 문자열을 파싱
                    String bboxStr = defectBbox.replace("[", "").replace("]", "");
                    String[] coords = bboxStr.split(",");
                    if (coords.length == 4) {
                        Float x1 = Float.parseFloat(coords[0].trim());
                        Float y1 = Float.parseFloat(coords[1].trim());
                        Float x2 = Float.parseFloat(coords[2].trim());
                        Float y2 = Float.parseFloat(coords[3].trim());
                        
                        // 중심점 계산
                        defectX = (x1 + x2) / 2;
                        defectY = (y1 + y2) / 2;
                        
                        // 너비와 높이 계산
                        defectWidth = x2 - x1;
                        defectHeight = y2 - y1;
                    }
                } catch (Exception e) {
                    System.out.println("=== bbox 파싱 중 오류: " + e.getMessage() + " ===");
                }
            }
            
            return new DefectDetectionResult(
                "defect",           // status: 결함 감지
                defectType,         // defectType: 결함 유형
                confidence,         // confidence: 신뢰도
                "결함 #" + (index + 1) + " 감지됨: " + defectType,  // message: 결함 번호 포함
                defectX, defectY, defectWidth, defectHeight, defectArea, defectBbox,  // 위치/크기 정보
                index               // 결함 순서
            );
            
        } catch (Exception e) {
            System.err.println("=== 개별 결함 변환 중 오류: " + e.getMessage() + " ===");
            return new DefectDetectionResult("error", null, 0.0, "개별 결함 변환 실패: " + e.getMessage());
        }
    }
    
    /**
     * Map에서 Float 값을 안전하게 추출
     */
    private Float extractFloatValue(Map<String, Object> prediction, String key) {
        try {
            Object value = prediction.get(key);
            if (value instanceof Number) {
                return ((Number) value).floatValue();
            } else if (value instanceof String) {
                return Float.parseFloat((String) value);
            }
        } catch (Exception e) {
            System.out.println("=== " + key + " 값 추출 실패: " + e.getMessage() + " ===");
        }
        return null;
    }
    
    /**
     * 바운딩 박스 정보를 JSON 문자열로 변환
     */
    private String extractBboxString(Map<String, Object> prediction) {
        try {
            Object bbox = prediction.get("bbox");
            if (bbox instanceof List) {
                List<?> bboxList = (List<?>) bbox;
                if (bboxList.size() >= 4) {
                    return String.format("[%.2f, %.2f, %.2f, %.2f]", 
                        bboxList.get(0), bboxList.get(1), bboxList.get(2), bboxList.get(3));
                }
            }
        } catch (Exception e) {
            System.out.println("=== 바운딩 박스 추출 실패: " + e.getMessage() + " ===");
        }
        return null;
    }

    /**
     * 결함 감지 로그를 DB에 저장
     */
    private void saveDefectDetectionLog(MultipartFile image, DefectDetectionResult result, int index) {
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
                log.setPressTime(result.getConfidence().floatValue());
            }
            
            // 결함 위치 및 크기 정보 설정
            log.setDefectX(result.getDefectX());
            log.setDefectY(result.getDefectY());
            log.setDefectWidth(result.getDefectWidth());
            log.setDefectHeight(result.getDefectHeight());
            log.setDefectArea(result.getDefectArea());
            log.setDefectBbox(result.getDefectBbox());
            
            // 여러 결함 구분을 위한 정보 설정
            log.setDefectIndex(result.getDefectIndex());
            log.setOriginalImageName(image.getOriginalFilename());
            
            // DB에 저장 (기존 repository() 메서드 사용)
            PaintingSurfaceDefectDetectionLog.repository().save(log);
            
            System.out.println("=== 로그 저장 완료: " + log.getId() + " ===");
            
        } catch (Exception e) {
            System.err.println("=== 로그 저장 중 오류: " + e.getMessage() + " ===");
            throw new RuntimeException("로그 저장 중 오류: " + e.getMessage());
        }
    }
}
