package com.example.demo.user;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;

@Controller
@RequestMapping("api/user")
public class UserController {

    private final UserRepository userRepository;

    @Autowired
    public UserController(UserRepository userRepository) {
        this.userRepository = userRepository;
    }

    @GetMapping("health")
    public ResponseEntity<String> health() {
        return new ResponseEntity<>("api/user is alive!", HttpStatus.OK);
    }

    @PostMapping("create")
    public ResponseEntity<String> createUser(@RequestBody UserEntityDao userEntityDao) {
        userRepository.save(new User(userEntityDao.username(), userEntityDao.password()));

        return new ResponseEntity<>("save user successfully!", HttpStatus.CREATED);
    }

    public record UserEntityDao(String username, String password) {

    }
}
