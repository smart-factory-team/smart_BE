package carsmartfactory.dto;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.AllArgsConstructor;

import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.NotEmpty;
import java.util.List;

/**
 * 시뮬레이터로부터 받는 센서 데이터 요청 DTO
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
public class SensorDataRequest {

    /**
     * 기계 ID (예: WELDING_MACHINE_001)
     */
    @NotNull(message = "기계 ID는 필수입니다")
    @JsonProperty("machineId")
    private String machineId;

    /**
     * 데이터 수집 시간 (ISO 형식)
     */
    @NotNull(message = "타임스탬프는 필수입니다")
    @JsonProperty("timestamp")
    private String timestamp;

    /**
     * 신호 타입 (cur: 전류, vib: 진동)
     */
    @NotNull(message = "신호 타입은 필수입니다")
    @JsonProperty("signalType")
    private String signalType;

    /**
     * 센서 값 배열 (전류: 1024개, 진동: 512개)
     */
    @NotEmpty(message = "센서 값은 필수입니다")
    @JsonProperty("sensorValues")
    private List<Double> sensorValues;

    /**
     * 데이터 소스 (예: simulator)
     */
    @JsonProperty("dataSource")
    private String dataSource = "simulator";

    // ✅ 수동으로 getter 메서드 추가 (Lombok이 작동하지 않는 경우)
    public String getMachineId() {
        return machineId;
    }

    public String getTimestamp() {
        return timestamp;
    }

    public String getSignalType() {
        return signalType;
    }

    public List<Double> getSensorValues() {
        return sensorValues;
    }

    public String getDataSource() {
        return dataSource;
    }

    // ✅ setter 메서드도 추가
    public void setMachineId(String machineId) {
        this.machineId = machineId;
    }

    public void setTimestamp(String timestamp) {
        this.timestamp = timestamp;
    }

    public void setSignalType(String signalType) {
        this.signalType = signalType;
    }

    public void setSensorValues(List<Double> sensorValues) {
        this.sensorValues = sensorValues;
    }

    public void setDataSource(String dataSource) {
        this.dataSource = dataSource;
    }
}