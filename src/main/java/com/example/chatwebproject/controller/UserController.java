package com.example.chatwebproject.controller;

import com.example.chatwebproject.model.entity.User;
import com.example.chatwebproject.model.dto.UserDto;
import com.example.chatwebproject.model.request.ChangePasswordRequest;
import com.example.chatwebproject.model.request.EditUserInfoRequest;
import com.example.chatwebproject.model.response.BaseResponse;
import com.example.chatwebproject.model.response.DownloadFileResponse;
import com.example.chatwebproject.model.response.RespFactory;
import com.example.chatwebproject.service.UserService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import javax.transaction.Transactional;
import javax.validation.Valid;
import java.util.List;

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
    public ResponseEntity<BaseResponse> addNewUser(@RequestBody @Valid User newUser){
        this.userService.save(newUser);
        return respFactory.success("Add new user succeed");
    }

    @PatchMapping("/{userId}")
    public ResponseEntity<BaseResponse> editUserInfo(
            @RequestBody @Valid EditUserInfoRequest request
            ){
        this.userService.updateUserInfo(request);
        return respFactory.success("Update succeed");
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

    @PostMapping("/upload-avatar")
    public ResponseEntity<BaseResponse> uploadAvatarUser(
            @RequestParam("file") MultipartFile file
    ) {
        return this.respFactory.success(this.userService.uploadAvatarUser(file));
    }

    @PostMapping("/download-avatar")
    public ResponseEntity<byte[]> downloadFile(
            @RequestParam("fileId") Long fileId
    ) {
        DownloadFileResponse downloadFileResponse = this.userService.downloadAvatarFile(fileId);
        return ResponseEntity.ok()
                .header(HttpHeaders.CONTENT_DISPOSITION, "attachment; filename=\"" + downloadFileResponse.getAvatarFileDto().getFileName() + "\"")
                .contentType(MediaType.parseMediaType(downloadFileResponse.getAvatarFileDto().getExtension()))
                .header(HttpHeaders.ACCESS_CONTROL_EXPOSE_HEADERS, "Content-Disposition")
                .body(downloadFileResponse.getBytes());
    }
}
