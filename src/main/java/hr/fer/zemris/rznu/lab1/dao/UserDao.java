package hr.fer.zemris.rznu.lab1.dao;

import hr.fer.zemris.rznu.lab1.model.User;
import org.springframework.data.jpa.repository.JpaRepository;

public interface UserDao extends JpaRepository<User, Long> {

    User findByUsername(String username);
}
