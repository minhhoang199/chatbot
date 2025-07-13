package com.example.chatwebproject.controller;


import com.example.chatwebproject.model.request.ChangeConnectionStatusRequest;
import com.example.chatwebproject.model.request.CreateConnectionRequest;
import com.example.chatwebproject.model.response.BaseResponse;
import com.example.chatwebproject.model.response.CreateConnectionResponse;
import com.example.chatwebproject.service.ConnectionService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import javax.validation.Valid;

@RestController
@RequestMapping("/api/v1/connections")
public class ConnectionController {
    private ConnectionService connectionService;

    public ConnectionController(ConnectionService connectionService) {
        this.connectionService = connectionService;
    }

    @PostMapping
    public ResponseEntity<BaseResponse> addNewConnection(@RequestBody @Valid CreateConnectionRequest request){
        return this.connectionService.createConnection(request);
    }

    @PatchMapping
    public ResponseEntity<String> changeStatus(
            @RequestBody @Valid ChangeConnectionStatusRequest changeConnectionStatusRequest){
        this.connectionService.changeConnectionStatus(changeConnectionStatusRequest);
        return ResponseEntity.ok("Create Connection success");
    }
}
