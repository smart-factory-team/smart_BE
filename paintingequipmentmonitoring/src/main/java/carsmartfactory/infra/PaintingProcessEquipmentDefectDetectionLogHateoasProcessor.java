package carsmartfactory.infra;

import carsmartfactory.domain.*;
import org.springframework.hateoas.EntityModel;
import org.springframework.hateoas.Link;
import org.springframework.hateoas.server.RepresentationModelProcessor;
import org.springframework.stereotype.Component;

@Component
public class PaintingProcessEquipmentDefectDetectionLogHateoasProcessor
    implements
        RepresentationModelProcessor<EntityModel<PaintingProcessEquipmentDefectDetectionLog>> {

    @Override
    public EntityModel<PaintingProcessEquipmentDefectDetectionLog> process(
        EntityModel<PaintingProcessEquipmentDefectDetectionLog> model
    ) {
        return model;
    }
}
