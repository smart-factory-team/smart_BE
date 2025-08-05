package carsmartfactory.infra;

import carsmartfactory.domain.*;
import org.springframework.hateoas.EntityModel;
import org.springframework.hateoas.Link;
import org.springframework.hateoas.server.RepresentationModelProcessor;
import org.springframework.stereotype.Component;

@Component
public class PressDefectDetectionLogHateoasProcessor
    implements
        RepresentationModelProcessor<EntityModel<PressDefectDetectionLog>> {

    @Override
    public EntityModel<PressDefectDetectionLog> process(
        EntityModel<PressDefectDetectionLog> model
    ) {
        return model;
    }
}
