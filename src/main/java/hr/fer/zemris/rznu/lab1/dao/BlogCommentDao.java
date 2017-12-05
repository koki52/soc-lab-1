package hr.fer.zemris.rznu.lab1.dao;

import hr.fer.zemris.rznu.lab1.model.BlogComment;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface BlogCommentDao extends JpaRepository<BlogComment, Long> {

    List<BlogComment> findAllByBlogPost_Id(Long id);

    BlogComment findByBlogPost_IdAndBlogPost_Author_UsernameAndId(
            Long postId,
            String postAuthor,
            Long id
    );
}
