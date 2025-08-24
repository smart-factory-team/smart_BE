package carsmartfactory.infra;

import carsmartfactory.domain.*;
import java.util.Optional;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.transaction.Transactional;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

//<<< Clean Arch / Inbound Adaptor

@RestController
// @RequestMapping(value="/posts")
@Transactional
public class PostController {

    @Autowired
    PostRepository postRepository;

    @GetMapping("/posts")
    public ResponseEntity<Iterable<Post>> getAllPosts() {
        Iterable<Post> posts = postRepository.findAll();
        return new ResponseEntity<>(posts, HttpStatus.OK);
    }
}
//>>> Clean Arch / Inbound Adaptor
