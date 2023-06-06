package com.example.chatwebproject.controller;

import com.example.chatwebproject.model.User;
import com.example.chatwebproject.model.vm.UserVM;
import com.example.chatwebproject.service.UserService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import javax.validation.Valid;
import java.text.SimpleDateFormat;
import java.util.Date;

@RestController
@RequestMapping("/api/v1/users")
public class UserController {
    private UserService userService;

    public UserController(UserService userService) {
        this.userService = userService;
    }

    @PostMapping
    public ResponseEntity<String> addNewUser(@RequestBody @Valid User newUser){
        this.userService.save(newUser);
        return ResponseEntity.ok("Add new user succeed");
    }

    @PatchMapping("/{userId}")
    public ResponseEntity<String> editUserInfo(
            @PathVariable("userId") Long userId,
            @RequestBody @Valid UserVM userVM
            ){
        this.userService.updateUserInfo(userId, userVM);
        return ResponseEntity.ok("Update succeed");
    }

    @PatchMapping("/{userId}/password")
    public ResponseEntity<String> changePassword(
            @PathVariable("userId") Long userId,
            @RequestBody String password
    ){
        this.userService.changePassword(userId, password);
        return ResponseEntity.ok("Change password succeed");
    }

}
