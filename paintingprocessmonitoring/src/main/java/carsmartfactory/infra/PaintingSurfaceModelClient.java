package carsmartfactory.infra;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.core.io.ByteArrayResource;
import org.springframework.util.LinkedMultiValueMap;
import org.springframework.util.MultiValueMap;

import carsmartfactory.infra.dto.PaintingSurfacePredictionResponseDto;
import java.util.Map;

@Service
public class PaintingSurfaceModelClient {

    @Value("${painting.surface.model.service.url:http://smart-factory-painting-ai-model:8002}")
    private String modelServiceUrl;

    private final RestTemplate restTemplate = new RestTemplate();

    public PaintingSurfacePredictionResponseDto predict(MultipartFile image, Float confidenceThreshold) {
        
        System.out.println("=== FastAPI 호출: " + modelServiceUrl + "/predict/file ===");
        
        try {
            HttpHeaders headers = new HttpHeaders();
            headers.setContentType(MediaType.MULTIPART_FORM_DATA);
            
            // FastAPI가 요구하는 형태로 변환
            MultiValueMap<String, Object> body = new LinkedMultiValueMap<>();
            body.add("image", new ByteArrayResource(image.getBytes()) {
                @Override
                public String getFilename() {
                    return image.getOriginalFilename();
                }
            });
            body.add("confidence_threshold", confidenceThreshold != null ? confidenceThreshold.toString() : "0.5");
            
            HttpEntity<MultiValueMap<String, Object>> request = new HttpEntity<>(body, headers);
            
            PaintingSurfacePredictionResponseDto response = restTemplate.postForObject(
                modelServiceUrl + "/predict/file", 
                request, 
                PaintingSurfacePredictionResponseDto.class
            );
            
            System.out.println("=== FastAPI 응답 성공 ===");
            
            // AI 모델 응답 상세 로깅
            if (response != null) {
                System.out.println("=== AI 모델 응답 상세 정보 ===");
                System.out.println("예측 결과 개수: " + (response.getPredictions() != null ? response.getPredictions().size() : 0));
                System.out.println("이미지 크기: " + response.getImageShape());
                System.out.println("신뢰도 임계값: " + response.getConfidenceThreshold());
                System.out.println("타임스탬프: " + response.getTimestamp());
                System.out.println("모델 소스: " + response.getModelSource());
                
                if (response.getPredictions() != null && !response.getPredictions().isEmpty()) {
                    System.out.println("=== 결함 상세 정보 ===");
                    for (int i = 0; i < response.getPredictions().size(); i++) {
                        Map<String, Object> prediction = response.getPredictions().get(i);
                        System.out.println("결함 " + (i + 1) + ":");
                        System.out.println("  클래스명: " + prediction.get("class_name"));
                        System.out.println("  신뢰도: " + prediction.get("confidence"));
                        System.out.println("  바운딩박스: " + prediction.get("bbox"));
                        System.out.println("  영역: " + prediction.get("area"));
                    }
                } else {
                    System.out.println("=== 결함 없음 - 정상 상태 ===");
                }
                System.out.println("================================");
            }
            
            return response;
            
        } catch (Exception e) {
            System.out.println("=== FastAPI 호출 실패: " + e.getMessage() + " ===");
            throw new RuntimeException("Painting surface model service call failed", e);
        }
    }
}
