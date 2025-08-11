package carsmartfactory.infra;

import carsmartfactory.domain.*;
import org.springframework.hateoas.EntityModel;
import org.springframework.hateoas.Link;
import org.springframework.hateoas.server.RepresentationModelProcessor;
import org.springframework.stereotype.Component;

@Component
public class PaintingSurfaceDefectDetectionLogHateoasProcessor
    implements
        RepresentationModelProcessor<EntityModel<PaintingSurfaceDefectDetectionLog>> {

    @Override
    public EntityModel<PaintingSurfaceDefectDetectionLog> process(
        EntityModel<PaintingSurfaceDefectDetectionLog> model
    ) {
        return model;
    }
}
