package carsmartfactory.domain;

import carsmartfactory.domain.*;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.rest.core.annotation.RepositoryRestResource;

//<<< PoEAA / Repository
@RepositoryRestResource(
        collectionResourceRel = "weldingMachineDefectDetectionLogs",
        path = "weldingMachineDefectDetectionLogs"
)
public interface WeldingMachineDefectDetectionLogRepository
        extends JpaRepository<WeldingMachineDefectDetectionLog, String> {

    // JpaRepository에는 save(), findById(), findAll(), delete() 등이 모두 포함됨
    // 추가 메서드가 필요하면 여기에 정의
}
//>>> PoEAA / Repository