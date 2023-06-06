package com.example.chatwebproject.controller;

import com.example.chatwebproject.model.Conversation;
import com.example.chatwebproject.model.enums.ConversationStatus;
import com.example.chatwebproject.model.vm.GroupConversationVM;
import com.example.chatwebproject.model.vm.InviteeVM;
import com.example.chatwebproject.model.vm.PrivateConversationVM;
import com.example.chatwebproject.service.ConversationService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import javax.validation.Valid;
import java.util.List;

@RestController
@RequestMapping("/api/v1/conversations")
public class ConversationController {
    private ConversationService conversationService;

    public ConversationController(ConversationService conversationService) {
        this.conversationService = conversationService;
    }

    @GetMapping
    public ResponseEntity<List<Conversation>> getAll() {
        return ResponseEntity.ok(conversationService.getAll());
    }

    @PostMapping("/groupConversation")
    public ResponseEntity<String> createGroupConversation(
            @RequestBody @Valid GroupConversationVM groupConversationVM
    ) {
        this.conversationService.addGroupConversation(groupConversationVM);
        return ResponseEntity.ok("Create group conversation succeed");
    }

    @PostMapping("/privateConversation")
    public ResponseEntity<String> createPrivateConversation(
            @RequestBody @Valid PrivateConversationVM privateConversationVM
    ) {
        this.conversationService.addNewPrivateConversation(privateConversationVM);
        return ResponseEntity.ok("Create private chat succeed");
    }

    @PostMapping("/{conversationId}")
    public ResponseEntity<String> addAccounts(
            @PathVariable("conversationId") Long conversationId,
            @RequestBody @Valid InviteeVM inviteeVm
    ) {
        this.conversationService.addMoreUser(inviteeVm, conversationId);
        return ResponseEntity.ok("Add more users succeed");
    }

    @PatchMapping("/{conversationId}")
    public ResponseEntity<String> changeStatus(
            @PathVariable("conversationId") Long conversationId,
            @RequestBody ConversationStatus conversationStatus
    ){
        this.conversationService.changeConversationStatus(conversationId, conversationStatus);
        return ResponseEntity.ok("Change conversation status succeed");
    }
}