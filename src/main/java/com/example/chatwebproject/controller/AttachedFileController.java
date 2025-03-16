package com.example.chatwebproject.controller;

import com.example.chatwebproject.model.dto.InviteeDto;
import com.example.chatwebproject.model.dto.RoomDto;
import com.example.chatwebproject.model.enums.RoomStatus;
import com.example.chatwebproject.model.request.ChangeUserListRequest;
import com.example.chatwebproject.model.request.GetListRoomRequest;
import com.example.chatwebproject.model.request.SaveRoomRequest;
import com.example.chatwebproject.model.response.RespBody;
import com.example.chatwebproject.model.response.RespFactory;
import com.example.chatwebproject.model.response.Result;
import com.example.chatwebproject.service.AttachedFileService;
import com.example.chatwebproject.service.RoomService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;

import javax.validation.Valid;
import java.util.List;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/v1/attached-files")
public class AttachedFileController {
    private final AttachedFileService attachedFileService;
    private final RespFactory respFactory;

    @PostMapping("")
    public ResponseEntity<RespBody> uploadFile(
            @RequestParam("file") MultipartFile file,
            @RequestParam("roomId") Long roomId
    ) {
        return this.respFactory.success(this.attachedFileService.saveFile(roomId, file));
    }
}