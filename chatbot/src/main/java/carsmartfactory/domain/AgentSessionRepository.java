package carsmartfactory.domain;

import carsmartfactory.domain.*;
import org.springframework.data.repository.PagingAndSortingRepository;
import org.springframework.data.rest.core.annotation.RepositoryRestResource;

//<<< PoEAA / Repository
@RepositoryRestResource(
    collectionResourceRel = "agentSessions",
    path = "agentSessions"
)
public interface AgentSessionRepository
    extends PagingAndSortingRepository<AgentSession, String> {}
