package carsmartfactory.infra;

import carsmartfactory.config.kafka.KafkaProcessor;
import carsmartfactory.domain.*;
import com.fasterxml.jackson.databind.DeserializationFeature;
import com.fasterxml.jackson.databind.ObjectMapper;
import javax.naming.NameParser;
import javax.naming.NameParser;
import javax.transaction.Transactional;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.cloud.stream.annotation.StreamListener;
import org.springframework.messaging.handler.annotation.Payload;
import org.springframework.stereotype.Service;

//<<< Clean Arch / Inbound Adaptor
@Service
@Transactional
public class PolicyHandler {

    @Autowired
    VehicleAssemblyProcessDefectDetectionLogRepository vehicleAssemblyProcessDefectDetectionLogRepository;

    @StreamListener(KafkaProcessor.INPUT)
    public void whatever(@Payload String eventString) {}

    @StreamListener(
        value = KafkaProcessor.INPUT,
        condition = "headers['type']=='IssueSolved'"
    )
    public void wheneverIssueSolved_IssueSolvedPolicy(
        @Payload IssueSolved issueSolved
    ) {
        IssueSolved event = issueSolved;
        System.out.println(
            "\n\n##### listener IssueSolvedPolicy : " + issueSolved + "\n\n"
        );

        // Sample Logic //
        VehicleAssemblyProcessDefectDetectionLog.issueSolvedPolicy(event);
    }
}
//>>> Clean Arch / Inbound Adaptor
