package hr.fer.zemris.rznu.lab1.rest;

import hr.fer.zemris.rznu.lab1.dao.BlogPostDao;
import hr.fer.zemris.rznu.lab1.dao.UserDao;
import hr.fer.zemris.rznu.lab1.dto.BlogPostDto;
import hr.fer.zemris.rznu.lab1.model.BlogPost;
import hr.fer.zemris.rznu.lab1.model.User;
import hr.fer.zemris.rznu.lab1.util.UserUtil;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.stream.Collectors;

@RestController
@RequestMapping(path = "/api/{user}/posts")
public class BlogPostRest {

    private UserUtil userUtil;
    private BlogPostDao blogPostDao;
    private UserDao userDao;

    @Autowired
    public BlogPostRest(UserUtil userUtil, BlogPostDao blogPostDao, UserDao userDao) {
        this.userUtil = userUtil;
        this.blogPostDao = blogPostDao;
        this.userDao = userDao;
    }

    @GetMapping
    public ResponseEntity getAllByUser(@PathVariable("user") String user) {
        if(userDao.findByUsername(user) == null) {
            return ResponseEntity
                    .status(HttpStatus.NOT_FOUND)
                    .body("User " + user + " not found!");
        }
        List<BlogPostDto> blogPosts = blogPostDao.findAllByAuthor_Username(user)
                .stream()
                .map(BlogPostDto::fromBlogPost)
                .collect(Collectors.toList());

        return ResponseEntity.ok(blogPosts);
    }

    @GetMapping(path = "/{id}")
    public ResponseEntity findOnePost(
            @PathVariable("user") String author,
            @PathVariable("id") Long id) {
        BlogPost post = blogPostDao.findByAuthor_UsernameAndId(author, id);
        if(post == null) {
            return ResponseEntity
                    .status(HttpStatus.NOT_FOUND)
                    .body("Blog post not found!");
        }
        return ResponseEntity.ok(BlogPostDto.fromBlogPost(post));
    }

    @PostMapping
    public ResponseEntity newBlogPost(
            @RequestBody BlogPost blogPost,
            @PathVariable("user") String username) {
        User user = userUtil.getCurrentUser();

        if(user == null) {
            return ResponseEntity
                    .status(HttpStatus.UNAUTHORIZED)
                    .body("You need to log in to post!");
        }
        if(!user.getUsername().equals(username)) {
            return ResponseEntity
                    .status(HttpStatus.FORBIDDEN)
                    .body("You can only post to your own blog!");
        }
        blogPost.setAuthor(user);
        blogPostDao.save(blogPost);

        return ResponseEntity.status(HttpStatus.CREATED).build();
    }

    @PutMapping(path = "/{id}")
    public ResponseEntity updateBlogPost(
            @PathVariable("user") String author,
            @PathVariable("id") Long postId,
            @RequestBody BlogPostDto update) {
        User user = userUtil.getCurrentUser();

        if(user == null) {
            return ResponseEntity
                    .status(HttpStatus.UNAUTHORIZED)
                    .body("You need to log in to post!");
        }
        BlogPost post = blogPostDao.findByAuthor_UsernameAndId(author, postId);

        if(post == null) {
            return ResponseEntity
                    .status(HttpStatus.NOT_FOUND)
                    .body("Blog post not found!");
        }
        if(!user.getUsername().equals(author)) {
            return ResponseEntity
                    .status(HttpStatus.FORBIDDEN)
                    .body("You can only edit your own blog posts!");
        }

        post.setTitle(update.getTitle());
        post.setBody(update.getBody());
        blogPostDao.save(post);

        return ResponseEntity.ok().build();
    }

    @DeleteMapping(path = "/{id}")
    public ResponseEntity deleteBlogPost(
            @PathVariable("user") String username,
            @PathVariable("id") Long postId) {
        User user = userUtil.getCurrentUser();

        if(user == null) {
            return ResponseEntity
                    .status(HttpStatus.UNAUTHORIZED)
                    .body("You need to log in to post!");
        }
        BlogPost post = blogPostDao.findOne(postId);

        if(post == null) {
            return ResponseEntity
                    .status(HttpStatus.NOT_FOUND)
                    .body("Blog post doesn't exist!");
        }
        if(!user.getUsername().equals(username) || post.getAuthor() != user) {
            return ResponseEntity
                    .status(HttpStatus.FORBIDDEN)
                    .body("You can only delete your own blog post!");
        }

        blogPostDao.delete(post);

        return ResponseEntity.ok().build();
    }
}
