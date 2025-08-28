package carsmartfactory.dto;

import com.fasterxml.jackson.annotation.JsonProperty;

/**
 * 실시간 모니터링 차트용 WebSocket 메시지 DTO 모든 예측 결과(정상/이상)를 차트에 전송하기 위한 데이터 구조
 */
public class MonitoringDataMessage {

    @JsonProperty("messageType")
    private String messageType; // "PREDICTION_RESULT", "SYSTEM_STATUS", "CONNECTION_ESTABLISHED"

    @JsonProperty("equipmentId")
    private String equipmentId; // "WELDING_MACHINE_1", "WELDING_MACHINE_2", etc.

    @JsonProperty("signalType")
    private String signalType; // "current", "vibration"

    @JsonProperty("timestamp")
    private long timestamp; // Unix timestamp (밀리초)

    @JsonProperty("predictionResult")
    private PredictionData predictionResult;

    @JsonProperty("systemMessage")
    private String systemMessage; // 시스템 메시지 (연결, 오류 등)

    // 기본 생성자
    public MonitoringDataMessage() {
    }

    // 예측 결과용 생성자
    public MonitoringDataMessage(String equipmentId, String signalType, PredictionData predictionResult) {
        this.messageType = "PREDICTION_RESULT";
        this.equipmentId = equipmentId;
        this.signalType = signalType;
        this.predictionResult = predictionResult;
        this.timestamp = System.currentTimeMillis();
    }

    // 시스템 메시지용 생성자
    public MonitoringDataMessage(String messageType, String systemMessage) {
        this.messageType = messageType;
        this.systemMessage = systemMessage;
        this.timestamp = System.currentTimeMillis();
    }

    // 내부 클래스: 예측 데이터
    public static class PredictionData {
        @JsonProperty("isAnomalous")
        private boolean isAnomalous; // true: 이상, false: 정상

        @JsonProperty("confidence")
        private double confidence; // 신뢰도 (0.0 ~ 1.0)

        @JsonProperty("anomalyScore")
        private double anomalyScore; // 이상 점수

        @JsonProperty("threshold")
        private double threshold; // 임계값

        @JsonProperty("status")
        private String status; // "NORMAL", "ANOMALY"

        @JsonProperty("sensorValues")
        private SensorSummary sensorValues; // 센서 요약 데이터

        // 기본 생성자
        public PredictionData() {
        }

        // 전체 생성자
        public PredictionData(boolean isAnomalous, double confidence, double anomalyScore,
                              double threshold, SensorSummary sensorValues) {
            this.isAnomalous = isAnomalous;
            this.confidence = confidence;
            this.anomalyScore = anomalyScore;
            this.threshold = threshold;
            this.status = isAnomalous ? "ANOMALY" : "NORMAL";
            this.sensorValues = sensorValues;
        }

        // Getters and Setters
        public boolean isAnomalous() {
            return isAnomalous;
        }

        public void setAnomalous(boolean anomalous) {
            this.isAnomalous = anomalous;
        }

        public double getConfidence() {
            return confidence;
        }

        public void setConfidence(double confidence) {
            this.confidence = confidence;
        }

        public double getAnomalyScore() {
            return anomalyScore;
        }

        public void setAnomalyScore(double anomalyScore) {
            this.anomalyScore = anomalyScore;
        }

        public double getThreshold() {
            return threshold;
        }

        public void setThreshold(double threshold) {
            this.threshold = threshold;
        }

        public String getStatus() {
            return status;
        }

        public void setStatus(String status) {
            this.status = status;
        }

        public SensorSummary getSensorValues() {
            return sensorValues;
        }

        public void setSensorValues(SensorSummary sensorValues) {
            this.sensorValues = sensorValues;
        }
    }

    // 내부 클래스: 센서 요약 데이터 (차트 표시용)
    public static class SensorSummary {
        @JsonProperty("min")
        private double min;

        @JsonProperty("max")
        private double max;

        @JsonProperty("average")
        private double average;

        @JsonProperty("standardDeviation")
        private double standardDeviation;

        @JsonProperty("sampleCount")
        private int sampleCount;

        // 기본 생성자
        public SensorSummary() {
        }

        // 전체 생성자
        public SensorSummary(double min, double max, double average,
                             double standardDeviation, int sampleCount) {
            this.min = min;
            this.max = max;
            this.average = average;
            this.standardDeviation = standardDeviation;
            this.sampleCount = sampleCount;
        }

        // Getters and Setters
        public double getMin() {
            return min;
        }

        public void setMin(double min) {
            this.min = min;
        }

        public double getMax() {
            return max;
        }

        public void setMax(double max) {
            this.max = max;
        }

        public double getAverage() {
            return average;
        }

        public void setAverage(double average) {
            this.average = average;
        }

        public double getStandardDeviation() {
            return standardDeviation;
        }

        public void setStandardDeviation(double standardDeviation) {
            this.standardDeviation = standardDeviation;
        }

        public int getSampleCount() {
            return sampleCount;
        }

        public void setSampleCount(int sampleCount) {
            this.sampleCount = sampleCount;
        }
    }

    // Getters and Setters
    public String getMessageType() {
        return messageType;
    }

    public void setMessageType(String messageType) {
        this.messageType = messageType;
    }

    public String getEquipmentId() {
        return equipmentId;
    }

    public void setEquipmentId(String equipmentId) {
        this.equipmentId = equipmentId;
    }

    public String getSignalType() {
        return signalType;
    }

    public void setSignalType(String signalType) {
        this.signalType = signalType;
    }

    public long getTimestamp() {
        return timestamp;
    }

    public void setTimestamp(long timestamp) {
        this.timestamp = timestamp;
    }

    public PredictionData getPredictionResult() {
        return predictionResult;
    }

    public void setPredictionResult(PredictionData predictionResult) {
        this.predictionResult = predictionResult;
    }

    public String getSystemMessage() {
        return systemMessage;
    }

    public void setSystemMessage(String systemMessage) {
        this.systemMessage = systemMessage;
    }

    @Override
    public String toString() {
        return "MonitoringDataMessage{" +
                "messageType='" + messageType + '\'' +
                ", equipmentId='" + equipmentId + '\'' +
                ", signalType='" + signalType + '\'' +
                ", timestamp=" + timestamp +
                ", predictionResult=" + predictionResult +
                ", systemMessage='" + systemMessage + '\'' +
                '}';
    }
}