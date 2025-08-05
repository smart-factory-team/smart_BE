package carsmartfactory.infra;

import carsmartfactory.domain.*;
import org.springframework.hateoas.EntityModel;
import org.springframework.hateoas.Link;
import org.springframework.hateoas.server.RepresentationModelProcessor;
import org.springframework.stereotype.Component;

@Component
public class AgentSessionHateoasProcessor
    implements RepresentationModelProcessor<EntityModel<AgentSession>> {

    @Override
    public EntityModel<AgentSession> process(EntityModel<AgentSession> model) {
        return model;
    }
}
