package carsmartfactory.domain;

import carsmartfactory.domain.*;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.rest.core.annotation.RepositoryRestResource;

//<<< PoEAA / Repository
@RepositoryRestResource(
    collectionResourceRel = "paintingProcessEquipmentDefectDetectionLogs",
    path = "paintingProcessEquipmentDefectDetectionLogs"
)
public interface PaintingProcessEquipmentDefectDetectionLogRepository
    extends
        JpaRepository<PaintingProcessEquipmentDefectDetectionLog, Long> {}
