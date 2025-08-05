package carsmartfactory.domain;

import carsmartfactory.domain.*;
import org.springframework.data.repository.PagingAndSortingRepository;
import org.springframework.data.rest.core.annotation.RepositoryRestResource;

//<<< PoEAA / Repository
@RepositoryRestResource(
    collectionResourceRel = "pressDefectDetectionLogs",
    path = "pressDefectDetectionLogs"
)
public interface PressDefectDetectionLogRepository
    extends PagingAndSortingRepository<PressDefectDetectionLog, String> {}
