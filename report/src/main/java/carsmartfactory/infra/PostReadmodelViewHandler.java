package carsmartfactory.infra;

import carsmartfactory.domain.*;
import java.io.IOException;
import java.util.List;
import java.util.Optional;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.messaging.handler.annotation.Payload;
import org.springframework.stereotype.Service;

@Service
public class PostReadmodelViewHandler {

    //<<< DDD / CQRS
    @Autowired
    private PostReadmodelRepository postReadmodelRepository;
    //>>> DDD / CQRS
}
