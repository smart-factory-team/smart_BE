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
            return response;
            
        } catch (Exception e) {
            System.out.println("=== FastAPI 호출 실패: " + e.getMessage() + " ===");
            throw new RuntimeException("Painting surface model service call failed", e);
        }
    }
}
