package com.example.chatwebproject.controller;

import com.example.chatwebproject.model.vm.ConnectionStatusVM;
import com.example.chatwebproject.model.vm.RequestConnectionVM;
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
    public ResponseEntity<String> addNewConnection(@RequestBody @Valid RequestConnectionVM requestConnectionVM){
        this.connectionService.createConnection(requestConnectionVM);
        return ResponseEntity.ok("Create Connection success");
    }

    @PatchMapping
    public ResponseEntity<String> changeStatus(
            @RequestBody @Valid ConnectionStatusVM connectionStatusVm){
        this.connectionService.changeConnectionStatus(connectionStatusVm);
        return ResponseEntity.ok("Create Connection success");
    }
}
