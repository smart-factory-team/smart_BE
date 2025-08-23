package carsmartfactory.domain;

import carsmartfactory.domain.*;
import java.util.Date;
import java.util.List;
import java.util.Optional;

import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.PagingAndSortingRepository;
import org.springframework.data.rest.core.annotation.RepositoryRestResource;
import org.springframework.data.rest.core.annotation.RestResource;

//<<< PoEAA / Repository
@RepositoryRestResource(collectionResourceRel = "comments", path = "comments")
public interface CommentRepository
    extends PagingAndSortingRepository<Comment, String> {
        @RestResource(exported = false)
        List<Comment> findByPostIdAndIsDeletedFalse(String postId);
        Comment save(Comment comment);
        Optional<Comment> findById(String id);
    }
