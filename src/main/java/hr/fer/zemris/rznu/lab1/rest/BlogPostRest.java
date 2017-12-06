package hr.fer.zemris.rznu.lab1.rest;

import hr.fer.zemris.rznu.lab1.dao.BlogPostDao;
import hr.fer.zemris.rznu.lab1.dto.BlogPostDto;
import hr.fer.zemris.rznu.lab1.model.BlogPost;
import hr.fer.zemris.rznu.lab1.model.User;
import hr.fer.zemris.rznu.lab1.util.Error;
import hr.fer.zemris.rznu.lab1.util.UserUtil;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.stream.Collectors;

@RestController
@RequestMapping(path = "/api/posts")
public class BlogPostRest {

    private UserUtil userUtil;
    private BlogPostDao blogPostDao;

    @Autowired
    public BlogPostRest(UserUtil userUtil, BlogPostDao blogPostDao) {
        this.userUtil = userUtil;
        this.blogPostDao = blogPostDao;
    }

    @GetMapping
    public ResponseEntity getAllPosts() {
        List<BlogPostDto> posts = blogPostDao.findAll()
                .stream()
                .map(BlogPostDto::fromBlogPost)
                .collect(Collectors.toList());

        return ResponseEntity.ok(posts);
    }

    @PostMapping
    public ResponseEntity newPost(@RequestBody BlogPost post) {
        User user = userUtil.getCurrentUser();
        if(user == null) {
            return Error.toResponseEntity(
                    HttpStatus.UNAUTHORIZED,
                    "You need to log in to create posts!"
            );
        }
        post.setAuthor(user);
        blogPostDao.save(post);

        return ResponseEntity.status(HttpStatus.CREATED).build();
    }

    @GetMapping(path = "/{id}")
    public ResponseEntity getOnePost(@PathVariable("id") Long id) {
        BlogPost post = blogPostDao.findOne(id);
        if(post == null) {
            return Error.toResponseEntity(HttpStatus.NOT_FOUND, "Post not found!");
        }

        return ResponseEntity.ok(BlogPostDto.fromBlogPost(post));
    }

    @PutMapping(path = "/{id}")
    public ResponseEntity updatePost(@PathVariable("id") Long id, @RequestBody BlogPostDto update) {
        User user = userUtil.getCurrentUser();
        if(user == null) {
            return Error.toResponseEntity(
                    HttpStatus.UNAUTHORIZED,
                    "You need to log in to edit posts!"
            );
        }
        BlogPost post = blogPostDao.findOne(id);
        if (post == null) {
            return Error.toResponseEntity(HttpStatus.NOT_FOUND, "Post not found!");
        }
        if(!post.getAuthor().equals(user)) {
            return Error.toResponseEntity(
                    HttpStatus.FORBIDDEN,
                    "You can only edit your own posts!"
            );
        }
        post.setTitle(update.getTitle());
        post.setBody(update.getBody());
        blogPostDao.save(post);

        return ResponseEntity.ok().build();
    }

    @DeleteMapping(path = "/{id}")
    public ResponseEntity deletePost(@PathVariable("id") Long id) {
        User user = userUtil.getCurrentUser();
        if(user == null) {
            return Error.toResponseEntity(
                    HttpStatus.UNAUTHORIZED,
                    "You need to log in to delete posts!"
            );
        }
        BlogPost post = blogPostDao.findOne(id);
        if (post == null) {
            return Error.toResponseEntity(HttpStatus.NOT_FOUND, "Post not found!");
        }
        if(!post.getAuthor().equals(user)) {
            return Error.toResponseEntity(
                    HttpStatus.FORBIDDEN,
                    "You can only delete your own posts!"
            );
        }
        blogPostDao.delete(post);

        return ResponseEntity.ok().build();
    }
}
