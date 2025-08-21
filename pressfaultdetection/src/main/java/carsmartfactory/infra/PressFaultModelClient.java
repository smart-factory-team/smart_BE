package carsmartfactory.infra;


import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

import carsmartfactory.infra.dto.PressFaultDataDto;
import carsmartfactory.infra.dto.PressFaultPredictionRequestDto;
import carsmartfactory.infra.dto.PressFaultPredictionResponseDto;

@Service
public class PressFaultModelClient {

    @Value("${press.fault.model.service.url:http://localhost:8004}")
    private String modelServiceUrl;

    private final RestTemplate restTemplate = new RestTemplate();

    public PressFaultPredictionResponseDto predict(PressFaultDataDto data) {
        
        System.out.println("=== FastAPI 호출: " + modelServiceUrl + "/predict ===");
        
        try {
            HttpHeaders headers = new HttpHeaders();
            headers.setContentType(MediaType.APPLICATION_JSON);
            
            // FastAPI가 요구하는 형태로 변환
            PressFaultPredictionRequestDto requestDto = new PressFaultPredictionRequestDto();
            requestDto.setAI0_Vibration(data.getAI0_Vibration());
            requestDto.setAI1_Vibration(data.getAI1_Vibration());
            requestDto.setAI2_Current(data.getAI2_Current());
            
            HttpEntity<PressFaultPredictionRequestDto> request = new HttpEntity<>(requestDto, headers);
            
            PressFaultPredictionResponseDto response = restTemplate.postForObject(
                modelServiceUrl + "/predict", 
                request, 
                PressFaultPredictionResponseDto.class
            );
            
            System.out.println("=== FastAPI 응답 성공 ===");
            return response;
            
        } catch (Exception e) {
            System.out.println("=== FastAPI 호출 실패: " + e.getMessage() + " ===");
            throw new RuntimeException("Press fault model service call failed", e);
        }
    }
}