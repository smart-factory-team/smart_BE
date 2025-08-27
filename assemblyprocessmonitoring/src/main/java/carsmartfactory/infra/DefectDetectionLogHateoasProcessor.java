package carsmartfactory.infra;

import carsmartfactory.domain.*;
import org.springframework.hateoas.EntityModel;
import org.springframework.hateoas.server.RepresentationModelProcessor;
import org.springframework.stereotype.Component;

@Component
public class DefectDetectionLogHateoasProcessor
    implements
        RepresentationModelProcessor<EntityModel<DefectDetectionLog>> {

    @Override
    public EntityModel<DefectDetectionLog> process(
        EntityModel<DefectDetectionLog> model
    ) {
        return model;
    }
}
