package carsmartfactory.domain;

import carsmartfactory.domain.*;
import org.springframework.data.repository.PagingAndSortingRepository;
import org.springframework.data.rest.core.annotation.RepositoryRestResource;

//<<< PoEAA / Repository
@RepositoryRestResource(
    collectionResourceRel = "userRegisterations",
    path = "userRegisterations"
)
public interface UserRegisterationRepository
    extends PagingAndSortingRepository<UserRegisteration, String> {}
