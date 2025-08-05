package carsmartfactory.infra;

import carsmartfactory.domain.*;
import org.springframework.hateoas.EntityModel;
import org.springframework.hateoas.Link;
import org.springframework.hateoas.server.RepresentationModelProcessor;
import org.springframework.stereotype.Component;

@Component
public class UserApprovalHateoasProcessor
    implements RepresentationModelProcessor<EntityModel<UserApproval>> {

    @Override
    public EntityModel<UserApproval> process(EntityModel<UserApproval> model) {
        return model;
    }
}
