package hr.fer.zemris.rznu.lab1.rest;

import hr.fer.zemris.rznu.lab1.dao.BlogCommentDao;
import hr.fer.zemris.rznu.lab1.dao.BlogPostDao;
import hr.fer.zemris.rznu.lab1.dto.BlogCommentDto;
import hr.fer.zemris.rznu.lab1.model.BlogComment;
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
@RequestMapping(path = "/api/users/{author}/posts/{postId}/comments")
public class NestedBlogCommentRest {

    private UserUtil userUtil;
    private BlogPostDao blogPostDao;
    private BlogCommentDao blogCommentDao;

    @Autowired
    public NestedBlogCommentRest(UserUtil userUtil, BlogPostDao blogPostDao, BlogCommentDao blogCommentDao) {
        this.userUtil = userUtil;
        this.blogPostDao = blogPostDao;
        this.blogCommentDao = blogCommentDao;
    }

    @GetMapping
    public ResponseEntity getAllForPost(
            @PathVariable("author") String author,
            @PathVariable("postId") Long postId) {
        if(blogPostDao.findByAuthor_UsernameAndId(author, postId) == null) {
            return Error.toResponseEntity(
                    HttpStatus.NOT_FOUND,
                    "Blog post was not found!"
            );
        }

        List<BlogCommentDto> comments = blogCommentDao.findAllByBlogPost_Id(postId)
                .stream()
                .map(BlogCommentDto::fromBlogComment)
                .collect(Collectors.toList());

        return ResponseEntity.ok(comments);
    }

    @GetMapping(path = "/{commentId}")
    public ResponseEntity getComment(
            @PathVariable("author") String author,
            @PathVariable("postId") Long postId,
            @PathVariable("commentId") Long commentId) {
        BlogComment comment = blogCommentDao.findByBlogPost_IdAndBlogPost_Author_UsernameAndId(postId, author, commentId);

        if(comment == null) {
            return Error.toResponseEntity(HttpStatus.NOT_FOUND, "Comment not found!");
        }
        return ResponseEntity.ok(BlogCommentDto.fromBlogComment(comment));
    }

    @PostMapping
    public ResponseEntity postComment(
            @PathVariable("author") String author,
            @PathVariable("postId") Long postId,
            @RequestBody BlogComment comment) {
        User user = userUtil.getCurrentUser();
        if(user == null) {
            return Error.toResponseEntity(
                    HttpStatus.UNAUTHORIZED,
                    "You need to log in to comment!"
            );
        }

        BlogPost post = blogPostDao.findByAuthor_UsernameAndId(author, postId);
        if(post == null) {
            return Error.toResponseEntity(
                    HttpStatus.NOT_FOUND,
                    "Blog post not found!"
            );
        }

        comment.setAuthor(user);
        comment.setBlogPost(post);
        blogCommentDao.save(comment);

        return ResponseEntity.status(HttpStatus.CREATED).build();
    }

    @PutMapping(path = "/{commentId}")
    public ResponseEntity updateComment(
            @PathVariable("author") String author,
            @PathVariable("postId") Long postId,
            @PathVariable("commentId") Long commentId,
            @RequestBody BlogCommentDto update) {
        User user = userUtil.getCurrentUser();
        if(user == null) {
            return Error.toResponseEntity(
                    HttpStatus.UNAUTHORIZED,
                    "You need to log in to comment!"
            );
        }

        BlogComment comment = blogCommentDao.findByBlogPost_IdAndBlogPost_Author_UsernameAndId(postId, author, commentId);
        if(comment == null) {
            return Error.toResponseEntity(
                    HttpStatus.NOT_FOUND,
                    "Comment not found!"
            );
        }
        if(comment.getAuthor() != user) {
            return Error.toResponseEntity(
                    HttpStatus.FORBIDDEN,
                    "You can only edit your own comments!"
            );
        }
        comment.setBody(update.getBody());
        blogCommentDao.save(comment);

        return ResponseEntity.ok().build();
    }

    @DeleteMapping(path = "/{commentId}")
    public ResponseEntity deleteComment(
            @PathVariable("author") String author,
            @PathVariable("postId") Long postId,
            @PathVariable("commentId") Long commentId) {
        User user = userUtil.getCurrentUser();
        if(user == null) {
            return Error.toResponseEntity(
                    HttpStatus.UNAUTHORIZED,
                    "You need to log in to comment!"
            );
        }

        BlogComment comment = blogCommentDao.findByBlogPost_IdAndBlogPost_Author_UsernameAndId(postId, author, commentId);
        if(comment == null) {
            return Error.toResponseEntity(
                    HttpStatus.NOT_FOUND,
                    "Comment not found!"
            );
        }
        if(comment.getAuthor() != user) {
            return Error.toResponseEntity(
                    HttpStatus.FORBIDDEN,
                    "You can only edit your own comments!"
            );
        }
        blogCommentDao.delete(comment);

        return ResponseEntity.ok().build();
    }
}
