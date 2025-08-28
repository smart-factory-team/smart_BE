package carsmartfactory.dto;

import com.fasterxml.jackson.annotation.JsonProperty;

/**
 * WebSocket을 통해 전송되는 이상 탐지 알림 메시지 DTO
 */
public class DefectNotificationMessage {

    @JsonProperty("eventType")
    private String eventType;

    @JsonProperty("message")
    private String message;

    @JsonProperty("equipmentData")
    private EquipmentData equipmentData;

    @JsonProperty("status")
    private String status;

    @JsonProperty("timestamp")
    private Long timestamp;

    // ✅ 기본 생성자
    public DefectNotificationMessage() {
    }

    // ✅ 전체 생성자
    public DefectNotificationMessage(String eventType, String message, EquipmentData equipmentData, String status,
                                     Long timestamp) {
        this.eventType = eventType;
        this.message = message;
        this.equipmentData = equipmentData;
        this.status = status;
        this.timestamp = timestamp;
    }

    // ✅ 장비 데이터 내부 클래스
    public static class EquipmentData {
        @JsonProperty("id")
        private Long id;

        @JsonProperty("name")
        private String name;

        @JsonProperty("title")
        private String title;

        @JsonProperty("status")
        private String status;

        @JsonProperty("isOperating")
        private Boolean isOperating;

        @JsonProperty("manager")
        private String manager;

        @JsonProperty("isConnected")
        private Boolean isConnected;

        @JsonProperty("operatingStatus")
        private String operatingStatus;

        @JsonProperty("machineId")
        private Long machineId;

        @JsonProperty("signalType")
        private String signalType;

        @JsonProperty("mae")
        private Double mae;

        @JsonProperty("threshold")
        private Double threshold;

        @JsonProperty("issue")
        private String issue;

        // 생성자
        public EquipmentData() {
        }

        public EquipmentData(Long id, String name, String title, String status, Boolean isOperating,
                             String manager, Boolean isConnected, String operatingStatus, Long machineId,
                             String signalType, Double mae, Double threshold, String issue) {
            this.id = id;
            this.name = name;
            this.title = title;
            this.status = status;
            this.isOperating = isOperating;
            this.manager = manager;
            this.isConnected = isConnected;
            this.operatingStatus = operatingStatus;
            this.machineId = machineId;
            this.signalType = signalType;
            this.mae = mae;
            this.threshold = threshold;
            this.issue = issue;
        }

        // Getters and Setters
        public Long getId() {
            return id;
        }

        public void setId(Long id) {
            this.id = id;
        }

        public String getName() {
            return name;
        }

        public void setName(String name) {
            this.name = name;
        }

        public String getTitle() {
            return title;
        }

        public void setTitle(String title) {
            this.title = title;
        }

        public String getStatus() {
            return status;
        }

        public void setStatus(String status) {
            this.status = status;
        }

        public Boolean getIsOperating() {
            return isOperating;
        }

        public void setIsOperating(Boolean isOperating) {
            this.isOperating = isOperating;
        }

        public String getManager() {
            return manager;
        }

        public void setManager(String manager) {
            this.manager = manager;
        }

        public Boolean getIsConnected() {
            return isConnected;
        }

        public void setIsConnected(Boolean isConnected) {
            this.isConnected = isConnected;
        }

        public String getOperatingStatus() {
            return operatingStatus;
        }

        public void setOperatingStatus(String operatingStatus) {
            this.operatingStatus = operatingStatus;
        }

        public Long getMachineId() {
            return machineId;
        }

        public void setMachineId(Long machineId) {
            this.machineId = machineId;
        }

        public String getSignalType() {
            return signalType;
        }

        public void setSignalType(String signalType) {
            this.signalType = signalType;
        }

        public Double getMae() {
            return mae;
        }

        public void setMae(Double mae) {
            this.mae = mae;
        }

        public Double getThreshold() {
            return threshold;
        }

        public void setThreshold(Double threshold) {
            this.threshold = threshold;
        }

        public String getIssue() {
            return issue;
        }

        public void setIssue(String issue) {
            this.issue = issue;
        }
    }

    /**
     * 이상 탐지 결과를 위한 팩토리 메서드
     */
    public static DefectNotificationMessage createDefectAlert(Long machineId, String signalType, String issue,
                                                              Double mae, Double threshold) {
        EquipmentData equipment = new EquipmentData(
                machineId, "용접기 " + machineId, "로봇 용접기 고장 탐지", "이상", false,
                "관리자", true, "비상정지", machineId, signalType, mae, threshold, issue
        );

        return new DefectNotificationMessage(
                "DEFECT_DETECTED",
                "용접기 " + machineId + "번에서 이상이 감지되었습니다.",
                equipment,
                "ANOMALY",
                System.currentTimeMillis()
        );
    }

    /**
     * 정상 복구를 위한 팩토리 메서드
     */
    public static DefectNotificationMessage createNormalAlert(Long machineId, String signalType) {
        EquipmentData equipment = new EquipmentData(
                machineId, "용접기 " + machineId, "로봇 용접기 고장 탐지", "정상", true,
                "관리자", true, "가동 중", machineId, signalType, null, null, null
        );

        return new DefectNotificationMessage(
                "SYSTEM_NORMAL",
                "용접기 " + machineId + "번이 정상 상태로 복구되었습니다.",
                equipment,
                "NORMAL",
                System.currentTimeMillis()
        );
    }

    // Getters and Setters
    public String getEventType() {
        return eventType;
    }

    public void setEventType(String eventType) {
        this.eventType = eventType;
    }

    public String getMessage() {
        return message;
    }

    public void setMessage(String message) {
        this.message = message;
    }

    public EquipmentData getEquipmentData() {
        return equipmentData;
    }

    public void setEquipmentData(EquipmentData equipmentData) {
        this.equipmentData = equipmentData;
    }

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }

    public Long getTimestamp() {
        return timestamp;
    }

    public void setTimestamp(Long timestamp) {
        this.timestamp = timestamp;
    }
}