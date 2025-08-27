package carsmartfactory.dto;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.AllArgsConstructor;

/**
 * 모델 서빙 서비스로부터 받는 예측 결과 DTO
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
public class ModelPredictionResponse {

    /**
     * 신호 타입 (cur 또는 vib)
     */
    @JsonProperty("signal_type")
    private String signalType;

    /**
     * MAE (Mean Absolute Error) 값
     */
    @JsonProperty("mae")
    private Double mae;

    /**
     * 임계값
     */
    @JsonProperty("threshold")
    private Double threshold;

    /**
     * 예측 상태 (normal 또는 anomaly)
     */
    @JsonProperty("status")
    private String status;

    /**
     * 이상 여부 확인
     */
    public boolean isAnomalous() {
        return "anomaly".equalsIgnoreCase(status);
    }

    /**
     * 정상 여부 확인
     */
    public boolean isNormal() {
        return "normal".equalsIgnoreCase(status);
    }

    // ✅ 수동으로 getter 메서드 추가
    public String getSignalType() {
        return signalType;
    }

    public Double getMae() {
        return mae;
    }

    public Double getThreshold() {
        return threshold;
    }

    public String getStatus() {
        return status;
    }

    // ✅ setter 메서드도 추가
    public void setSignalType(String signalType) {
        this.signalType = signalType;
    }

    public void setMae(Double mae) {
        this.mae = mae;
    }

    public void setThreshold(Double threshold) {
        this.threshold = threshold;
    }

    public void setStatus(String status) {
        this.status = status;
    }
}