package carsmartfactory.controller;

import carsmartfactory.dto.DefectDetectionResponseList;
import carsmartfactory.dto.MonitoringResponse;
import carsmartfactory.service.ApiService;
import carsmartfactory.service.DefectDetectionService;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.client.RestTemplate;
import org.springframework.web.multipart.MultipartFile;

import java.util.HashMap;
import java.util.Map;

@RestController
@AllArgsConstructor
@Slf4j
@RequestMapping("/vehicleAssemblyProcessDefectDetectionLogs")
public class SimulationController {

    private RestTemplate restTemplate;
    private DefectDetectionService defectDetectionService;
    private ApiService apiService;

    // 이미지를 받아서 모델 서버에 보내는 API
    @PostMapping("/predict/file")
    public ResponseEntity<String> receiveImage(@RequestParam MultipartFile file, @RequestParam Long machineId) {
        // AI 모델 서버로 이미지 전송
        String result = apiService.sendImage(file);

        // 결과를 어딘가 저장 (메모리나 DB)
        defectDetectionService.saveResult(result, file, machineId);

        return ResponseEntity.ok("처리완료");
    }

    // 웹에서 결과 조회하는 API
    @GetMapping("/defect-detection/{machineId}")
    public ResponseEntity<DefectDetectionResponseList> getDefectDetectionResults(@PathVariable Long machineId) {
        return ResponseEntity.ok(defectDetectionService.getDefectDetectionsByMachineId(machineId));
    }

    @GetMapping("/defect-detection/{machineId}/{productCount}")
    public ResponseEntity<MonitoringResponse> getNewDefectDetectionResults(@PathVariable Long machineId, @PathVariable Long productCount) {
        return ResponseEntity.ok(defectDetectionService.getDefectDetectionsByMachineId(machineId,  productCount));
    }
}
