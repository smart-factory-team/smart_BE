package carsmartfactory.infra;

import carsmartfactory.domain.*;
import org.springframework.hateoas.EntityModel;
import org.springframework.hateoas.Link;
import org.springframework.hateoas.server.RepresentationModelProcessor;
import org.springframework.stereotype.Component;

@Component
public class UserRegisterationHateoasProcessor
    implements RepresentationModelProcessor<EntityModel<UserRegisteration>> {

    @Override
    public EntityModel<UserRegisteration> process(
        EntityModel<UserRegisteration> model
    ) {
        return model;
    }
}
