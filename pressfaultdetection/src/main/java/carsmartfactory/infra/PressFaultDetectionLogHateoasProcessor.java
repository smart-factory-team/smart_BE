package carsmartfactory.infra;

import carsmartfactory.domain.*;
import org.springframework.hateoas.EntityModel;
import org.springframework.hateoas.Link;
import org.springframework.hateoas.server.RepresentationModelProcessor;
import org.springframework.stereotype.Component;

@Component
public class PressFaultDetectionLogHateoasProcessor
    implements
        RepresentationModelProcessor<EntityModel<PressFaultDetectionLog>> {

    @Override
    public EntityModel<PressFaultDetectionLog> process(
        EntityModel<PressFaultDetectionLog> model
    ) {
        return model;
    }
}
