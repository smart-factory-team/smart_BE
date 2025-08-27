package carsmartfactory.infra;

import carsmartfactory.domain.*;
import com.fasterxml.jackson.databind.DeserializationFeature;
import com.fasterxml.jackson.databind.ObjectMapper;
import jakarta.transaction.Transactional;
import java.util.function.Consumer;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.messaging.Message;
import org.springframework.stereotype.Service;

//<<< Clean Arch / Inbound Adaptor
@Service
@Transactional
public class PolicyHandler {

    @Autowired
    PostRepository postRepository;

    @Autowired
    CommentRepository commentRepository;

    @Autowired
    ReportRepository reportRepository;

    @Bean
    public Consumer<String> eventIn() {
        return eventString -> {
            // A new consumer is created to replace the @StreamListener.
            // Business logic for event processing can be added here.
        };
    }
}
//>>> Clean Arch / Inbound Adaptor
