package carsmartfactory.infra;

import carsmartfactory.domain.*;
import org.springframework.hateoas.EntityModel;
import org.springframework.hateoas.Link;
import org.springframework.hateoas.server.RepresentationModelProcessor;
import org.springframework.stereotype.Component;

@Component
public class VehicleAssemblyProcessDefectDetectionLogHateoasProcessor
    implements
        RepresentationModelProcessor<EntityModel<VehicleAssemblyProcessDefectDetectionLog>> {

    @Override
    public EntityModel<VehicleAssemblyProcessDefectDetectionLog> process(
        EntityModel<VehicleAssemblyProcessDefectDetectionLog> model
    ) {
        return model;
    }
}
