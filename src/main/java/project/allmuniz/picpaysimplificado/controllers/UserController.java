package project.allmuniz.picpaysimplificado.controllers;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import project.allmuniz.picpaysimplificado.domain.user.User;
import project.allmuniz.picpaysimplificado.dtos.UserDTO;
import project.allmuniz.picpaysimplificado.services.UserService;

import java.util.List;

@RestController()
@RequestMapping("/users")
@Tag(name = "Users", description = "Users Manager")
public class UserController {

    private final UserService userService;

    public UserController(UserService userService) {
        this.userService = userService;
    }

    @PostMapping
    @Operation(description = "Create",
            summary = "User create")
    public ResponseEntity<User> createUser(@RequestBody UserDTO user) {
        User newUser = userService.createUser(user);
        return new ResponseEntity<>(newUser, HttpStatus.CREATED);
    }

    @GetMapping
    @Operation(description = "Find",
            summary = "Find all users")
    public ResponseEntity<List<User>> getAllUsers() {
        List<User> users = this.userService.getAllUsers();
        return new ResponseEntity<>(users, HttpStatus.OK);
    }
}
