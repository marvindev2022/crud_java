package com.example.ToDoList.user;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.autoconfigure.integration.IntegrationProperties.Error;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/users")
public class UserController {

    @Autowired
    private IUserRepository userRepository;

    @PostMapping("/")

    public ResponseEntity create(@RequestBody UserModel userModel) {
        var userExist = this.userRepository.findByUsername(userModel.getUsername());
        if (userExist != null) {
            System.out.println("User already exists");
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body("User already exists");
        }

        var userCReated = this.userRepository.save(userModel);
        return ResponseEntity.status(HttpStatus.CREATED).body(userCReated);
    }

}
