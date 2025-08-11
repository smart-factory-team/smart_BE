package carsmartfactory.domain;

import carsmartfactory.domain.*;
import org.springframework.data.repository.PagingAndSortingRepository;
import org.springframework.data.rest.core.annotation.RepositoryRestResource;

//<<< PoEAA / Repository
@RepositoryRestResource(
    collectionResourceRel = "paintingSurfaceDefectDetectionLogs",
    path = "paintingSurfaceDefectDetectionLogs"
)
public interface PaintingSurfaceDefectDetectionLogRepository
    extends
        PagingAndSortingRepository<PaintingSurfaceDefectDetectionLog, String> {}
