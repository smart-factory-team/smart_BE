package carsmartfactory.domain;

import carsmartfactory.domain.*;
import org.springframework.data.repository.PagingAndSortingRepository;
import org.springframework.data.rest.core.annotation.RepositoryRestResource;

//<<< PoEAA / Repository
@RepositoryRestResource(
    collectionResourceRel = "userApprovals",
    path = "userApprovals"
)
public interface UserApprovalRepository
    extends PagingAndSortingRepository<UserApproval, String> {}
