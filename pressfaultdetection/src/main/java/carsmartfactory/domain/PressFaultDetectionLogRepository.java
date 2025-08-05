package carsmartfactory.domain;

import carsmartfactory.domain.*;
import org.springframework.data.repository.PagingAndSortingRepository;
import org.springframework.data.rest.core.annotation.RepositoryRestResource;

//<<< PoEAA / Repository
@RepositoryRestResource(
    collectionResourceRel = "pressFaultDetectionLogs",
    path = "pressFaultDetectionLogs"
)
public interface PressFaultDetectionLogRepository
    extends PagingAndSortingRepository<PressFaultDetectionLog, String> {}
