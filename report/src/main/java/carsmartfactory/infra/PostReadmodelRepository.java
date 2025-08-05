package carsmartfactory.infra;

import carsmartfactory.domain.*;
import java.util.List;
import org.springframework.data.repository.PagingAndSortingRepository;
import org.springframework.data.rest.core.annotation.RepositoryRestResource;

@RepositoryRestResource(
    collectionResourceRel = "postReadmodels",
    path = "postReadmodels"
)
public interface PostReadmodelRepository
    extends PagingAndSortingRepository<PostReadmodel, Long> {}
