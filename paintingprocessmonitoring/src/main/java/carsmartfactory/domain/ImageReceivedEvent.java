package carsmartfactory.domain;

import org.springframework.context.ApplicationEvent;
import org.springframework.web.multipart.MultipartFile;
import carsmartfactory.infra.dto.PaintingSurfacePredictionResponseDto;

public class ImageReceivedEvent extends ApplicationEvent {
    
    private final MultipartFile image;
    private final String serviceName;
    private final long eventTimestamp;
    private final PaintingSurfacePredictionResponseDto aiModelResult;
    
    public ImageReceivedEvent(MultipartFile image) {
        super(image);
        this.image = image;
        this.serviceName = "painting-surface-defect-detection";
        this.eventTimestamp = System.currentTimeMillis();
        this.aiModelResult = null;
    }
    
    public ImageReceivedEvent(MultipartFile image, PaintingSurfacePredictionResponseDto aiModelResult) {
        super(image);
        this.image = image;
        this.serviceName = "painting-surface-defect-detection";
        this.eventTimestamp = System.currentTimeMillis();
        this.aiModelResult = aiModelResult;
    }
    
    public MultipartFile getImage() {
        return image;
    }
    
    public String getServiceName() {
        return serviceName;
    }
    
    public long getEventTimestamp() {
        return eventTimestamp;
    }
    
    public String getFileName() {
        return image.getOriginalFilename();
    }
    
    public long getFileSize() {
        return image.getSize();
    }
    
    public String getContentType() {
        return image.getContentType();
    }
    
    public PaintingSurfacePredictionResponseDto getAiModelResult() {
        return aiModelResult;
    }
}
