package com.example.chatwebproject.controller;

import com.example.chatwebproject.dto.request.CreateConnectionRequest;
import com.example.chatwebproject.dto.response.CreateConnectionResponse;
import com.example.chatwebproject.dto.request.ChangeConnectionStatusRequest;
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
    public CreateConnectionResponse addNewConnection(@RequestBody @Valid CreateConnectionRequest request){
        return this.connectionService.createConnection(request);
    }

    @PatchMapping
    public ResponseEntity<String> changeStatus(
            @RequestBody @Valid ChangeConnectionStatusRequest changeConnectionStatusRequest){
        this.connectionService.changeConnectionStatus(changeConnectionStatusRequest);
        return ResponseEntity.ok("Create Connection success");
    }
}
