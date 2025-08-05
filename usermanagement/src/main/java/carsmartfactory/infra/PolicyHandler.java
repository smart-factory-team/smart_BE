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
    UserRegisterationRepository userRegisterationRepository;

    @Autowired
    UserRepository userRepository;

    @StreamListener(KafkaProcessor.INPUT)
    public void whatever(@Payload String eventString) {}

    @StreamListener(
        value = KafkaProcessor.INPUT,
        condition = "headers['type']=='UserRegistrationApproved'"
    )
    public void wheneverUserRegistrationApproved_EnableUserAccount(
        @Payload UserRegistrationApproved userRegistrationApproved
    ) {
        UserRegistrationApproved event = userRegistrationApproved;
        System.out.println(
            "\n\n##### listener EnableUserAccount : " +
            userRegistrationApproved +
            "\n\n"
        );

        // Sample Logic //
        User.enableUserAccount(event);
    }

    @StreamListener(
        value = KafkaProcessor.INPUT,
        condition = "headers['type']=='UserRegistrationRejected'"
    )
    public void wheneverUserRegistrationRejected_DisableUseAccount(
        @Payload UserRegistrationRejected userRegistrationRejected
    ) {
        UserRegistrationRejected event = userRegistrationRejected;
        System.out.println(
            "\n\n##### listener DisableUseAccount : " +
            userRegistrationRejected +
            "\n\n"
        );

        // Sample Logic //
        User.disableUseAccount(event);
    }
}
//>>> Clean Arch / Inbound Adaptor
