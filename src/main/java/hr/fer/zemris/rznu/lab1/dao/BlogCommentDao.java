package hr.fer.zemris.rznu.lab1.dao;

import hr.fer.zemris.rznu.lab1.model.BlogComment;
import org.springframework.data.jpa.repository.JpaRepository;

public interface BlogCommentDao extends JpaRepository<BlogComment, Long> {


}
