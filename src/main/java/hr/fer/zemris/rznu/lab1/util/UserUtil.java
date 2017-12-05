package hr.fer.zemris.rznu.lab1.util;

import hr.fer.zemris.rznu.lab1.dao.UserDao;
import hr.fer.zemris.rznu.lab1.model.User;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.authentication.AnonymousAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Component;

@Component
public class UserUtil {

    private UserDao userDao;

    @Autowired
    public UserUtil(UserDao userDao) {
        this.userDao = userDao;
    }

    public User getCurrentUser() {
        Authentication auth = SecurityContextHolder.getContext().getAuthentication();
        if(auth.getClass().equals(AnonymousAuthenticationToken.class)) {
            return null;
        }
        return userDao.findByUsername(auth.getName());
    }
}
