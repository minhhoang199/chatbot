package com.example.chatwebproject.controller;

import com.example.chatwebproject.constant.DomainCode;
import com.example.chatwebproject.exception.ChatApplicationException;
import com.example.chatwebproject.exception.ValidationRequestException;
import com.example.chatwebproject.model.entity.User;
import com.example.chatwebproject.model.dto.UserDto;
import com.example.chatwebproject.model.enums.OTPType;
import com.example.chatwebproject.model.request.ChangePasswordRequest;
import com.example.chatwebproject.model.request.ForgotPasswordRequest;
import com.example.chatwebproject.model.request.OTPGenerateRequest;
import com.example.chatwebproject.model.response.BaseResponse;
import com.example.chatwebproject.model.response.RespFactory;
import com.example.chatwebproject.service.UserService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import javax.transaction.Transactional;
import javax.validation.Valid;
import java.util.List;
import java.util.Optional;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/v1/users")
public class UserController {
    private final UserService userService;
    private final RespFactory respFactory;

    @GetMapping("/search-by-email")
    public ResponseEntity<BaseResponse> searchByContent(@RequestParam(value = "email") String email){
        List<UserDto> userDtos = this.userService.searchByEmail(email);
        return this.respFactory.success(userDtos);
    }

    @PostMapping
    public ResponseEntity<String> addNewUser(@RequestBody @Valid User newUser){
        this.userService.save(newUser);
        return ResponseEntity.ok("Add new user succeed");
    }

    @PatchMapping("/{userId}")
    public ResponseEntity<String> editUserInfo(
            @RequestBody @Valid UserDto userVM
            ){
        this.userService.updateUserInfo(userVM);
        return ResponseEntity.ok("Update succeed");
    }

    @GetMapping("/friends")
    public ResponseEntity<BaseResponse> getFriends(){
        return respFactory.success(this.userService.getFriends());
    }

    @GetMapping("/recent-chat-user")
    public ResponseEntity<BaseResponse> getRecentUserChat(){
        return respFactory.success(this.userService.getRecentUserChat());
    }

    @PutMapping("/change-password")
    @Transactional
    public ResponseEntity<?> changePassword(@RequestBody @Valid ChangePasswordRequest changePasswordRequest) {
        this.userService.changePassword(changePasswordRequest);
        return this.respFactory.success();
    }
}
