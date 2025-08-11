package carsmartfactory.domain;

import carsmartfactory.domain.*;
import org.springframework.data.repository.PagingAndSortingRepository;
import org.springframework.data.rest.core.annotation.RepositoryRestResource;

//<<< PoEAA / Repository
@RepositoryRestResource(
    collectionResourceRel = "paintingProcessEquipmentDefectDetectionLogs",
    path = "paintingProcessEquipmentDefectDetectionLogs"
)
public interface PaintingProcessEquipmentDefectDetectionLogRepository
    extends
        PagingAndSortingRepository<PaintingProcessEquipmentDefectDetectionLog, String> {}
