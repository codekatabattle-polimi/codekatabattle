package it.polimi.codekatabattle.controllers;

import it.polimi.codekatabattle.models.User;
import it.polimi.codekatabattle.services.UserService;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequestMapping("users")
public class UserController {

    private final UserService userService;

    public UserController(UserService userService) {
        this.userService = userService;
    }

    @GetMapping
    List<User> getAllUsers() {
        return this.userService.findAll();
    }

}
