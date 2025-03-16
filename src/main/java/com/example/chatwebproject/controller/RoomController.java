package com.example.chatwebproject.controller;

import com.example.chatwebproject.model.dto.RoomDto;
import com.example.chatwebproject.model.enums.RoomStatus;
import com.example.chatwebproject.model.request.ChangeUserListRequest;
import com.example.chatwebproject.model.request.GetListRoomRequest;
import com.example.chatwebproject.model.request.SaveRoomRequest;
import com.example.chatwebproject.model.dto.InviteeDto;
import com.example.chatwebproject.model.response.RespBody;
import com.example.chatwebproject.model.response.RespFactory;
import com.example.chatwebproject.model.response.Result;
import com.example.chatwebproject.service.RoomService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import javax.validation.Valid;
import java.util.List;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/v1/rooms")
public class RoomController {
    private final RoomService roomService;
    private final RespFactory respFactory;

    @PostMapping
    public ResponseEntity<RespBody> createRoom(
            @RequestBody @Valid SaveRoomRequest request
    ) {
        return this.respFactory.success(this.roomService.addNewRoom(request));
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
    public ResponseEntity<Result> getAllRoomsByUserId(@PathVariable("id") Long id){
        GetListRoomRequest request = new GetListRoomRequest();
        request.setUserId(id);
        List<RoomDto> rooms = this.roomService.getAllByUserId(request);
        return ResponseEntity.ok(new Result("201", "Success", rooms));
    }

    @PutMapping("/outRoom/{roomId}")
    public ResponseEntity<RespBody> outRoom(@PathVariable("roomId") Long roomId){
        this.roomService.outRoom(roomId);
        return this.respFactory.success("Success");
    }

    @PutMapping("/add-users")
    public ResponseEntity<RespBody> addUserToRoom(@RequestBody @Valid ChangeUserListRequest request){
        this.roomService.addUserToRoom(request.getEmails(), request.getRoomId());
        return this.respFactory.success("Success");
    }

    @PutMapping("/remove-users")
    public ResponseEntity<RespBody> removeUsersToRoom(@RequestBody @Valid ChangeUserListRequest request){
        this.roomService.removeUsersToRoom(request.getEmails(), request.getRoomId());
        return this.respFactory.success("Success");
    }

    @PutMapping("/change-name/{roomId}")
    public ResponseEntity<RespBody> changeRoomName(@PathVariable("roomId") Long roomId, @RequestBody SaveRoomRequest request){
        this.roomService.changeRoomName(roomId, request.getName());
        return this.respFactory.success("Success");
    }
}