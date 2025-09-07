package com.example.chatwebproject.controller;


import com.example.chatwebproject.model.request.ChangeFriendshipStatusRequest;
import com.example.chatwebproject.model.request.CreateFriendshipRequest;
import com.example.chatwebproject.model.response.BaseResponse;
import com.example.chatwebproject.model.response.RespFactory;
import com.example.chatwebproject.service.FriendshipService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import javax.validation.Valid;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/v1/friendships")
public class FriendshipController {
    private final FriendshipService friendshipService;
    private final RespFactory respFactory;

    @PostMapping
    public ResponseEntity<BaseResponse> addNewFriendship(@RequestBody @Valid CreateFriendshipRequest request){
        return respFactory.success(this.friendshipService.createFriendship(request));
    }

    @PatchMapping
    public ResponseEntity<BaseResponse> changeStatus(
            @RequestBody @Valid ChangeFriendshipStatusRequest changeFriendshipStatusRequest){
        return respFactory.success(this.friendshipService.changeStatus(changeFriendshipStatusRequest));
    }

    @GetMapping("/incoming-request")
    public ResponseEntity<BaseResponse> getIncomingRequest(){
        return respFactory.success(this.friendshipService.getIncomingRequest());
    }

    @GetMapping("/outgoing-request")
    public ResponseEntity<BaseResponse> getOutgoingRequest(){
        return respFactory.success(this.friendshipService.getOutgoingRequest());
    }

    @GetMapping("/accepted-friend")
    public ResponseEntity<BaseResponse> getAcceptedFriend(@RequestParam(value = "findingEmail", required = false) String findingEmail){
        return respFactory.success(this.friendshipService.getAcceptedFriend(findingEmail));
    }

    @GetMapping("/blocked-friend")
    public ResponseEntity<BaseResponse> getBlockedFriend(@RequestParam(value = "findingEmail", required = false) String findingEmail){
        return respFactory.success(this.friendshipService.getBlockedFriend(findingEmail));
    }
}
