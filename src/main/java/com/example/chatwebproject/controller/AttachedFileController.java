package com.example.chatwebproject.controller;

import com.example.chatwebproject.model.response.BaseResponse;
import com.example.chatwebproject.model.response.RespFactory;
import com.example.chatwebproject.service.AttachedFileService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/v1/attached-files")
public class AttachedFileController {
    private final AttachedFileService attachedFileService;
    private final RespFactory respFactory;

    @PostMapping("")
    public ResponseEntity<BaseResponse> uploadFile(
            @RequestParam("file") MultipartFile file,
            @RequestParam("roomId") Long roomId
    ) {
        return this.respFactory.success(this.attachedFileService.saveFile(roomId, file));
    }
}