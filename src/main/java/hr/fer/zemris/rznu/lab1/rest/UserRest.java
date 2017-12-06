package hr.fer.zemris.rznu.lab1.rest;

import hr.fer.zemris.rznu.lab1.dao.UserDao;
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
@RequestMapping(path = "/api")
public class UserRest {

    private UserDao userDao;

    @Autowired
    public UserRest(UserDao userDao) {
        this.userDao = userDao;
    }

    @GetMapping("/users")
    public ResponseEntity getAllUsers() {
        List<String> users = userDao.findAll()
                .stream()
                .map(User::getUsername)
                .collect(Collectors.toList());

        return ResponseEntity.ok(users);
    }

    @PostMapping("/register")
    public ResponseEntity register(@RequestBody User user) {
        if(userDao.findByUsername(user.getUsername()) != null) {
            return Error.toResponseEntity(
                    HttpStatus.BAD_REQUEST,
                    "Username is already taken!"
            );
        }
        userDao.save(user);

        return ResponseEntity.ok().build();
    }
}
