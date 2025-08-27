package carsmartfactory.domain;

import carsmartfactory.domain.*;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.rest.core.annotation.RepositoryRestResource;

//<<< PoEAA / Repository
@RepositoryRestResource(
    collectionResourceRel = "pressFaultDetectionLogs",
    path = "pressFaultDetectionLogs"
)
public interface PressFaultDetectionLogRepository
    extends JpaRepository<PressFaultDetectionLog, String> {}
