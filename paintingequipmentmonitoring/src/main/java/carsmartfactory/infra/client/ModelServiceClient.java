package carsmartfactory.infra.client;

import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;

@FeignClient(name = "model-service", url = "${feign.client.model-service.url}")
public interface ModelServiceClient {

    @PostMapping("/predict/")
    ModelPredictionResponse predict(@RequestBody ModelPredictionRequest request);
}
