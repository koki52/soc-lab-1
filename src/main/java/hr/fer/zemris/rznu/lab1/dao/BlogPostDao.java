package hr.fer.zemris.rznu.lab1.dao;

import hr.fer.zemris.rznu.lab1.model.BlogPost;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface BlogPostDao extends JpaRepository<BlogPost, Long> {

    List<BlogPost> findAllByAuthor_Username(String username);

    BlogPost findByAuthor_UsernameAndId(String username, Long id);
}
