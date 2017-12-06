package hr.fer.zemris.rznu.lab1.rest;

import hr.fer.zemris.rznu.lab1.dao.BlogCommentDao;
import hr.fer.zemris.rznu.lab1.dto.BlogCommentDto;
import hr.fer.zemris.rznu.lab1.model.BlogComment;
import hr.fer.zemris.rznu.lab1.model.User;
import hr.fer.zemris.rznu.lab1.util.Error;
import hr.fer.zemris.rznu.lab1.util.UserUtil;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.stream.Collectors;

@RestController
@RequestMapping(path = "/comments")
public class BlogCommentRest {

    private BlogCommentDao blogCommentDao;
    private UserUtil userUtil;

    public BlogCommentRest(BlogCommentDao blogCommentDao, UserUtil userUtil) {
        this.blogCommentDao = blogCommentDao;
        this.userUtil = userUtil;
    }

    @GetMapping
    public ResponseEntity getAllComments() {
        List<BlogCommentDto> comments = blogCommentDao.findAll()
                .stream()
                .map(BlogCommentDto::fromBlogComment)
                .collect(Collectors.toList());
        return ResponseEntity.ok(comments);
    }

    @GetMapping(path = "/{id}")
    public ResponseEntity getOneComment(@PathVariable("id") Long id) {
        BlogComment comment = blogCommentDao.findOne(id);
        if(comment == null) {
            return Error.toResponseEntity(HttpStatus.NOT_FOUND, "Comment not found!");
        }

        return ResponseEntity.ok(BlogCommentDto.fromBlogComment(comment));
    }

    @PutMapping(path = "/{id}")
    public ResponseEntity updateComment(@PathVariable("id") Long id, @RequestBody BlogCommentDto update) {
        BlogComment comment = blogCommentDao.findOne(id);
        if(comment == null) {
            return Error.toResponseEntity(HttpStatus.NOT_FOUND, "Comment not found!");
        }
        User user = userUtil.getCurrentUser();
        if(user == null) {
            return Error.toResponseEntity(
                    HttpStatus.UNAUTHORIZED,
                    "You need to log in to edit comments!"
            );
        }
        if(!comment.getAuthor().equals(user)) {
            return Error.toResponseEntity(
                    HttpStatus.FORBIDDEN,
                    "You can only edit your own comments!"
            );
        }
        comment.setBody(update.getBody());
        blogCommentDao.save(comment);

        return ResponseEntity.ok().build();
    }

    @DeleteMapping(path = "/{id}")
    public ResponseEntity deleteComment(@PathVariable("id") Long id) {
        BlogComment comment = blogCommentDao.findOne(id);
        if(comment == null) {
            return Error.toResponseEntity(HttpStatus.NOT_FOUND, "Comment not found!");
        }
        User user = userUtil.getCurrentUser();
        if(user == null) {
            return Error.toResponseEntity(
                    HttpStatus.UNAUTHORIZED,
                    "You need to log in to delete comments!"
            );
        }
        if(!comment.getAuthor().equals(user)) {
            return Error.toResponseEntity(
                    HttpStatus.FORBIDDEN,
                    "You can only delete your own comments!"
            );
        }
        blogCommentDao.delete(comment);

        return ResponseEntity.ok().build();
    }
}
