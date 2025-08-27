package carsmartfactory.domain;

import org.springframework.data.repository.PagingAndSortingRepository;
import org.springframework.data.rest.core.annotation.RepositoryRestResource;

import java.util.List;

//<<< PoEAA / Repository
@RepositoryRestResource(
    collectionResourceRel = "vehicleAssemblyProcessDefectDetectionLogs",
    path = "vehicleAssemblyProcessDefectDetectionLogs"
)
public interface DefectDetectionLogRepository
    extends
        PagingAndSortingRepository<DefectDetectionLog, String> {

    DefectDetectionLog save(DefectDetectionLog log);

    // machineId별 조회 (최신순)
    List<DefectDetectionLog> findByMachineIdOrderByTimeStampDesc(Long machineId);

    Long countByCategoryAndMachineId(String category, Long machineId);
}
