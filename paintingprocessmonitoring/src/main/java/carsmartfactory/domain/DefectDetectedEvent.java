package carsmartfactory.domain;

import org.springframework.context.ApplicationEvent;

public class DefectDetectedEvent extends ApplicationEvent {
    
    private final String fileName;
    private final String defectType;
    private final Double confidence;
    private final String serviceName;
    private final long eventTimestamp;
    
    public DefectDetectedEvent(DefectDetectionResult result, String fileName) {
        super(result);
        this.fileName = fileName;
        this.defectType = result.getDefectType();
        this.confidence = result.getConfidence();
        this.serviceName = "painting-surface-defect-detection";
        this.eventTimestamp = System.currentTimeMillis();
    }
    
    public String getFileName() {
        return fileName;
    }
    
    public String getDefectType() {
        return defectType;
    }
    
    public Double getConfidence() {
        return confidence;
    }
    
    public String getServiceName() {
        return serviceName;
    }
    
    public long getEventTimestamp() {
        return eventTimestamp;
    }
    
    public DefectDetectionResult getResult() {
        return (DefectDetectionResult) getSource();
    }
}
