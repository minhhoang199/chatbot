package com.example.chatwebproject.controller;

import com.example.chatwebproject.model.Room;
import com.example.chatwebproject.model.enums.RoomStatus;
import com.example.chatwebproject.model.dto.RoomDto;
import com.example.chatwebproject.model.dto.InviteeDto;
import com.example.chatwebproject.service.RoomService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import javax.validation.Valid;
import java.util.List;

@RestController
@RequestMapping("/api/v1/conversations")
public class RoomController {
    private RoomService roomService;

    public RoomController(RoomService roomService) {
        this.roomService = roomService;
    }

    @GetMapping
    public ResponseEntity<List<Room>> getAll() {
        return ResponseEntity.ok(roomService.getAll());
    }

    @PostMapping("/groupConversation")
    public ResponseEntity<String> createRoom(
            @RequestBody @Valid RoomDto groupConversationVM
    ) {
        this.roomService.addNewRoom(groupConversationVM);
        return ResponseEntity.ok("Create group conversation succeed");
    }

    @PostMapping("/{conversationId}")
    public ResponseEntity<String> addAccounts(
            @PathVariable("conversationId") Long conversationId,
            @RequestBody @Valid InviteeDto inviteeDto
    ) {
        this.roomService.addMoreUser(inviteeDto, conversationId);
        return ResponseEntity.ok("Add more users succeed");
    }

    @PatchMapping("/{conversationId}")
    public ResponseEntity<String> changeStatus(
            @PathVariable("conversationId") Long conversationId,
            @RequestBody RoomStatus roomStatus
    ){
        this.roomService.changeConversationStatus(conversationId, roomStatus);
        return ResponseEntity.ok("Change conversation status succeed");
    }
}