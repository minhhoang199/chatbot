package com.example.chatwebproject.controller;

import com.example.chatwebproject.dto.request.GetListRoomRequest;
import com.example.chatwebproject.dto.response.ResponseObject;
import com.example.chatwebproject.model.Room;
import com.example.chatwebproject.model.enums.RoomStatus;
import com.example.chatwebproject.model.dto.RoomDto;
import com.example.chatwebproject.model.dto.InviteeDto;
import com.example.chatwebproject.service.RoomService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import javax.validation.Valid;
import java.util.List;
import java.util.Set;

@RestController
@RequestMapping("/api/v1/rooms")
public class RoomController {
    private RoomService roomService;

    public RoomController(RoomService roomService) {
        this.roomService = roomService;
    }

    @PostMapping
    public ResponseEntity<String> createRoom(
            @RequestBody @Valid RoomDto roomDto
    ) {
        this.roomService.addNewRoom(roomDto);
        return ResponseEntity.ok("Create room succeed");
    }

    @PostMapping("/{id}")
    public ResponseEntity<String> addAccounts(
            @PathVariable("id") Long conversationId,
            @RequestBody @Valid InviteeDto inviteeDto
    ) {
        this.roomService.addMoreUser(inviteeDto, conversationId);
        return ResponseEntity.ok("Add more users succeed");
    }

    @PatchMapping("/{id}")
    public ResponseEntity<String> changeStatus(
            @PathVariable("id") Long id,
            @RequestBody RoomStatus roomStatus
    ){
        this.roomService.changeConversationStatus(id, roomStatus);
        return ResponseEntity.ok("Change conversation status succeed");
    }

    @GetMapping("/{id}")
    public ResponseEntity<ResponseObject> getAllRoomsByUserId(
            @PathVariable("id") Long id
    ){
        GetListRoomRequest request = new GetListRoomRequest();
        request.setUserId(id);
        List<RoomDto> rooms = this.roomService.getAllByUserId(request);
        return ResponseEntity.ok(new ResponseObject("201", "Success", rooms));
    }
}