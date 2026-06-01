package com.example.chatwebproject.controller;

import com.example.chatwebproject.model.dto.RoomDto;
import com.example.chatwebproject.model.dto.UserDto;
import com.example.chatwebproject.model.enums.RoomStatus;
import com.example.chatwebproject.model.request.ChangeRoomNameRequest;
import com.example.chatwebproject.model.request.ChangeUserListRequest;
import com.example.chatwebproject.model.request.GetListRoomRequest;
import com.example.chatwebproject.model.request.SaveRoomRequest;
import com.example.chatwebproject.model.dto.InviteeDto;
import com.example.chatwebproject.model.response.BaseResponse;
import com.example.chatwebproject.model.response.RespFactory;
import com.example.chatwebproject.service.RoomService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import javax.validation.Valid;
import java.util.List;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/v1/rooms")
public class RoomController {
    private final RoomService roomService;
    private final RespFactory respFactory;

    @PostMapping
    public ResponseEntity<BaseResponse> createRoom(
            @RequestBody @Valid SaveRoomRequest request
    ) {
        return this.respFactory.success(this.roomService.addNewRoom(request));
    }

//    @PostMapping("/{id}/add-members")
//    public ResponseEntity<String> addAccounts(
//            @PathVariable("id") Long conversationId,
//            @RequestBody @Valid InviteeDto inviteeDto
//    ) {
//        this.roomService.addMoreUser(inviteeDto, conversationId);
//        return ResponseEntity.ok("Add more users succeed");
//    }

    @PatchMapping("/{id}")
    public ResponseEntity<String> changeStatus(
            @PathVariable("id") Long id,
            @RequestBody RoomStatus roomStatus
    ){
        this.roomService.changeConversationStatus(id, roomStatus);
        return ResponseEntity.ok("Change conversation status succeed");
    }

    @GetMapping ("/{id}")
    public ResponseEntity<BaseResponse> getDetail(@PathVariable("id") Long id){
        return this.respFactory.success(this.roomService.findRoomById(id));
    }

    @GetMapping("/users/{userId}")
    public ResponseEntity<BaseResponse> getAllRoomsByUserId(@PathVariable("userId") Long userId){
        GetListRoomRequest request = new GetListRoomRequest();
        request.setUserId(userId);
        List<RoomDto> rooms = this.roomService.getAllByUserId();
        return this.respFactory.success(rooms);
    }

    @GetMapping("/{roomId}/members")
    public ResponseEntity<BaseResponse> getMembers(@PathVariable("roomId") Long roomId){
        List<UserDto> members = this.roomService.getMembers(roomId);
        return this.respFactory.success(members);
    }

    @PutMapping("/{roomId}/outRoom")
    public ResponseEntity<BaseResponse> outRoom(@PathVariable("roomId") Long roomId){
        this.roomService.outRoom(roomId);
        return this.respFactory.success("Success");
    }

    @PutMapping("/add-users")
    public ResponseEntity<BaseResponse> addUserToRoom(@RequestBody @Valid ChangeUserListRequest request){
        this.roomService.addUserToRoom(request.getEmails(), request.getRoomId());
        return this.respFactory.success("Success");
    }

    @PutMapping("/remove-users")
    public ResponseEntity<BaseResponse> removeUsersToRoom(@RequestBody @Valid ChangeUserListRequest request){
        this.roomService.removeUsersToRoom(request.getEmails(), request.getRoomId());
        return this.respFactory.success("Success");
    }

    @PutMapping("/{roomId}/change-name")
    public ResponseEntity<BaseResponse> changeRoomName(@PathVariable("roomId") Long roomId, @RequestBody ChangeRoomNameRequest request){
        this.roomService.changeRoomName(roomId, request.getName());
        return this.respFactory.success("Success");
    }

    @GetMapping("/get-by-email")
    public ResponseEntity<BaseResponse> getByEmail(@RequestParam("email") String email){
        return this.respFactory.success(this.roomService.getRoomByEmail(email));
    }

    @DeleteMapping("/{roomId}")
    public ResponseEntity<BaseResponse> deleteRoom(@PathVariable("roomId") Long roomId){
        return this.respFactory.success(this.roomService.deleteRoom(roomId));
    }

    @PostMapping("/upload-avatar")
    public ResponseEntity<BaseResponse> uploadAvatarGroup(
            @RequestParam("file") MultipartFile file,
            @RequestParam("roomId") Long roomId
    ) {
        return this.respFactory.success(this.roomService.uploadAvatarGroup(file, roomId));
    }
}