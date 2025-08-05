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
    AgentSessionRepository agentSessionRepository;

    @Autowired
    IssueRepository issueRepository;

    @StreamListener(KafkaProcessor.INPUT)
    public void whatever(@Payload String eventString) {}

    @StreamListener(
        value = KafkaProcessor.INPUT,
        condition = "headers['type']=='DefectDetectionLogCreated'"
    )
    public void wheneverDefectDetectionLogCreated_OccuredIssue(
        @Payload DefectDetectionLogCreated defectDetectionLogCreated
    ) {
        DefectDetectionLogCreated event = defectDetectionLogCreated;
        System.out.println(
            "\n\n##### listener OccuredIssue : " +
            defectDetectionLogCreated +
            "\n\n"
        );

        // Sample Logic //
        Issue.occuredIssue(event);
    }

    @StreamListener(
        value = KafkaProcessor.INPUT,
        condition = "headers['type']=='PressFaultDetectionSaved'"
    )
    public void wheneverPressFaultDetectionSaved_OccuredIssue(
        @Payload PressFaultDetectionSaved pressFaultDetectionSaved
    ) {
        PressFaultDetectionSaved event = pressFaultDetectionSaved;
        System.out.println(
            "\n\n##### listener OccuredIssue : " +
            pressFaultDetectionSaved +
            "\n\n"
        );

        // Sample Logic //
        Issue.occuredIssue(event);
    }

    @StreamListener(
        value = KafkaProcessor.INPUT,
        condition = "headers['type']=='PressDefectPredictionSaved'"
    )
    public void wheneverPressDefectPredictionSaved_OccuredIssue(
        @Payload PressDefectPredictionSaved pressDefectPredictionSaved
    ) {
        PressDefectPredictionSaved event = pressDefectPredictionSaved;
        System.out.println(
            "\n\n##### listener OccuredIssue : " +
            pressDefectPredictionSaved +
            "\n\n"
        );

        // Sample Logic //
        Issue.occuredIssue(event);
    }

    @StreamListener(
        value = KafkaProcessor.INPUT,
        condition = "headers['type']=='WeldingMachineDefectSaved'"
    )
    public void wheneverWeldingMachineDefectSaved_OccuredIssue(
        @Payload WeldingMachineDefectSaved weldingMachineDefectSaved
    ) {
        WeldingMachineDefectSaved event = weldingMachineDefectSaved;
        System.out.println(
            "\n\n##### listener OccuredIssue : " +
            weldingMachineDefectSaved +
            "\n\n"
        );

        // Sample Logic //
        Issue.occuredIssue(event);
    }

    @StreamListener(
        value = KafkaProcessor.INPUT,
        condition = "headers['type']=='PaintingProcessEquipmentDefectSaved'"
    )
    public void wheneverPaintingProcessEquipmentDefectSaved_OccuredIssue(
        @Payload PaintingProcessEquipmentDefectSaved paintingProcessEquipmentDefectSaved
    ) {
        PaintingProcessEquipmentDefectSaved event =
            paintingProcessEquipmentDefectSaved;
        System.out.println(
            "\n\n##### listener OccuredIssue : " +
            paintingProcessEquipmentDefectSaved +
            "\n\n"
        );

        // Sample Logic //
        Issue.occuredIssue(event);
    }

    @StreamListener(
        value = KafkaProcessor.INPUT,
        condition = "headers['type']=='PaintingSurfaceDefectSaved'"
    )
    public void wheneverPaintingSurfaceDefectSaved_OccuredIssue(
        @Payload PaintingSurfaceDefectSaved paintingSurfaceDefectSaved
    ) {
        PaintingSurfaceDefectSaved event = paintingSurfaceDefectSaved;
        System.out.println(
            "\n\n##### listener OccuredIssue : " +
            paintingSurfaceDefectSaved +
            "\n\n"
        );

        // Sample Logic //
        Issue.occuredIssue(event);
    }

    @StreamListener(
        value = KafkaProcessor.INPUT,
        condition = "headers['type']=='IssueSolved'"
    )
    public void wheneverIssueSolved_SolvedIssue(
        @Payload IssueSolved issueSolved
    ) {
        IssueSolved event = issueSolved;
        System.out.println(
            "\n\n##### listener SolvedIssue : " + issueSolved + "\n\n"
        );

        // Sample Logic //
        AgentSession.solvedIssue(event);
    }
}
//>>> Clean Arch / Inbound Adaptor
