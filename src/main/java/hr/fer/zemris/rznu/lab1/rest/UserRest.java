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
    private UserUtil userUtil;

    @Autowired
    public UserRest(UserDao userDao, UserUtil userUtil) {
        this.userDao = userDao;
        this.userUtil = userUtil;
    }

    @GetMapping("/users")
    public ResponseEntity getAllUsers() {
        List<String> users = userDao.findAll()
                .stream()
                .map(User::getUsername)
                .collect(Collectors.toList());

        return ResponseEntity.ok(users);
    }

    @PostMapping("/users")
    public ResponseEntity register(@RequestBody User user) {
        if(userDao.findByUsername(user.getUsername()) != null) {
            return Error.toResponseEntity(
                    HttpStatus.BAD_REQUEST,
                    "Username is already taken!"
            );
        }
        userDao.save(user);

        return ResponseEntity.status(HttpStatus.CREATED).build();
    }

    @PutMapping("/users")
    public ResponseEntity updatePassword(@RequestBody User update) {
        User user = userUtil.getCurrentUser();
        if(user == null) {
            return Error.toResponseEntity(
                    HttpStatus.UNAUTHORIZED,
                    "You need to log in to change your password!"
            );
        }
        user.setPassword(update.getPassword());
        userDao.save(user);

        return ResponseEntity.ok().build();
    }

    @DeleteMapping("/users")
    public ResponseEntity deleteUser() {
        User user = userUtil.getCurrentUser();
        if(user == null) {
            return Error.toResponseEntity(
                    HttpStatus.UNAUTHORIZED,
                    "You need to log in to delete your account!"
            );
        }
        userDao.delete(user);

        return ResponseEntity.ok().build();
    }
}
