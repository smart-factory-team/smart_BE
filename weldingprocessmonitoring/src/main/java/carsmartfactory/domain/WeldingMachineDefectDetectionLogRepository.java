package carsmartfactory.domain;

import carsmartfactory.domain.*;
import org.springframework.data.repository.PagingAndSortingRepository;
import org.springframework.data.rest.core.annotation.RepositoryRestResource;

//<<< PoEAA / Repository
@RepositoryRestResource(
    collectionResourceRel = "weldingMachineDefectDetectionLogs",
    path = "weldingMachineDefectDetectionLogs"
)
public interface WeldingMachineDefectDetectionLogRepository
    extends
        PagingAndSortingRepository<WeldingMachineDefectDetectionLog, String> {}
