package hr.fer.zemris.rznu.lab1.rest;

import hr.fer.zemris.rznu.lab1.dao.BlogPostDao;
import hr.fer.zemris.rznu.lab1.dao.UserDao;
import hr.fer.zemris.rznu.lab1.model.BlogPost;
import hr.fer.zemris.rznu.lab1.model.User;
import hr.fer.zemris.rznu.lab1.util.UserUtil;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
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

    @GetMapping(path = "/api/{user}/posts")
    public ResponseEntity getAllByUser(@PathVariable("user") String user) {
        if(userDao.findByUsername(user) == null) {
            return ResponseEntity
                    .status(HttpStatus.NOT_FOUND)
                    .body("User " + user + " not found!");
        }
        return ResponseEntity.ok(blogPostDao.findAllByAuthor_Username(user));
    }

    @PostMapping(path = "/api/{user}/posts")
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
                    .body("You must post to your own blog!");
        }
        blogPost.setAuthor(user);
        blogPostDao.save(blogPost);
        return ResponseEntity.status(HttpStatus.CREATED).build();
    }
}
