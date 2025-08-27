package carsmartfactory.infra;

import carsmartfactory.domain.*;
import carsmartfactory.infra.util.UserIdMaskingUtil;
import java.util.Map;
import java.util.Optional;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.transaction.Transactional;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Sort;
import org.springframework.data.web.SortDefault;
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

    @Autowired
    UserIdMaskingUtil userIdMaskingUtil;

    @GetMapping("/posts")
    public ResponseEntity<Iterable<Post>> getAllPosts(
        @SortDefault(sort = "createdAt", direction = Sort.Direction.DESC) Sort sort
    ) {
        Iterable<Post> posts = postRepository.findAll(sort);
        posts.forEach(post -> post.setUserId(userIdMaskingUtil.maskString(post.getUserId())));
        return new ResponseEntity<>(posts, HttpStatus.OK);
    }

    @GetMapping("/posts/{id}")
    public ResponseEntity<Post> getPostById(@PathVariable String id) {
        return postRepository.findById(id).map(post -> {
            post.setUserId(userIdMaskingUtil.maskString(post.getUserId()));
            return new ResponseEntity<>(post, HttpStatus.OK);
        }).orElseGet(() -> new ResponseEntity<>(HttpStatus.NOT_FOUND));
    }

    @PostMapping("/posts")
    public ResponseEntity<Post> createPost(@RequestBody Post post) {
        Post savedPost = postRepository.save(post);
        savedPost.setUserId(userIdMaskingUtil.maskString(savedPost.getUserId()));
        return new ResponseEntity<>(savedPost, HttpStatus.CREATED);
    }

    @PatchMapping("/posts/{id}")
    public ResponseEntity<Post> updatePost(@PathVariable String id, @RequestBody Map<String, Object> updates) {
        return postRepository.findById(id).map(post -> {
            updates.forEach((key, value) -> {
                if (key.equals("title")) {
                    post.setTitle((String) value);
                }
                if (key.equals("content")) {
                    post.setContent((String) value);
                }
            });
            Post updatedPost = postRepository.save(post);
            updatedPost.setUserId(userIdMaskingUtil.maskString(updatedPost.getUserId()));
            return new ResponseEntity<>(updatedPost, HttpStatus.OK);
        }).orElseGet(() -> new ResponseEntity<>(HttpStatus.NOT_FOUND));
    }

    @DeleteMapping("/posts/{id}")
    public ResponseEntity<Void> deletePost(@PathVariable String id) {
        if (postRepository.existsById(id)) {
            postRepository.deleteById(id);
            return new ResponseEntity<>(HttpStatus.NO_CONTENT);
        } else {
            return new ResponseEntity<>(HttpStatus.NOT_FOUND);
        }
    }
}
//>>> Clean Arch / Inbound Adaptor
