package carsmartfactory.domain;

import carsmartfactory.domain.*;
import org.springframework.data.repository.PagingAndSortingRepository;
import org.springframework.data.rest.core.annotation.RepositoryRestResource;

//<<< PoEAA / Repository
@RepositoryRestResource(
    collectionResourceRel = "vehicleAssemblyProcessDefectDetectionLogs",
    path = "vehicleAssemblyProcessDefectDetectionLogs"
)
public interface VehicleAssemblyProcessDefectDetectionLogRepository
    extends
        PagingAndSortingRepository<VehicleAssemblyProcessDefectDetectionLog, String> {}
