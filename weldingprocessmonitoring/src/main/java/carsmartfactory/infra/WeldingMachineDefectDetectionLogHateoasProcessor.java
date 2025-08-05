package carsmartfactory.infra;

import carsmartfactory.domain.*;
import org.springframework.hateoas.EntityModel;
import org.springframework.hateoas.Link;
import org.springframework.hateoas.server.RepresentationModelProcessor;
import org.springframework.stereotype.Component;

@Component
public class WeldingMachineDefectDetectionLogHateoasProcessor
    implements
        RepresentationModelProcessor<EntityModel<WeldingMachineDefectDetectionLog>> {

    @Override
    public EntityModel<WeldingMachineDefectDetectionLog> process(
        EntityModel<WeldingMachineDefectDetectionLog> model
    ) {
        return model;
    }
}
