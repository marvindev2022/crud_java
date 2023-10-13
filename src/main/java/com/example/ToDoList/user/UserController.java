package com.example.ToDoList.user;

import java.util.UUID;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import at.favre.lib.crypto.bcrypt.BCrypt;

@RestController
@RequestMapping("/users")
public class UserController {

    @Autowired
    private IUserRepository userRepository;

    @PostMapping("/")

    public ResponseEntity create(@RequestBody UserModel userModel) {
        var userExist = this.userRepository.findByUsername(userModel.getUsername());
        if (userExist != null) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body("User already exists");
        }
        var passwordHashed = BCrypt.withDefaults().hashToString(12, userModel.getPassword().toCharArray());
        userModel.setPassword(passwordHashed);

        var userCReated = this.userRepository.save(userModel);
        return ResponseEntity.status(HttpStatus.CREATED).body(userCReated);
    }
    @PutMapping("/{userId}")
    public ResponseEntity update(@RequestBody UserModel userModel, @PathVariable UUID userId) {
        var userExist = this.userRepository.findById(userId);
        if (userExist.isEmpty()) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body("User not found");
        }
        var user = userExist.get();
        user.setUsername(userModel.getUsername());
        user.setPassword(userModel.getPassword());
         var passwordHashed = BCrypt.withDefaults().hashToString(12, userModel.getPassword().toCharArray());
        user.setPassword(passwordHashed);
        var userUpdated = this.userRepository.save(user);
        return ResponseEntity.status(HttpStatus.OK).body(userUpdated);
    }
    @GetMapping("/{userId}")
    public ResponseEntity getOne(@PathVariable UUID userId) {
        var userExist = this.userRepository.findById(userId);
        if (userExist.isEmpty()) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body("User not found");
        }
        var user = userExist.get();
        return ResponseEntity.status(HttpStatus.OK).body(user);
    }

}
