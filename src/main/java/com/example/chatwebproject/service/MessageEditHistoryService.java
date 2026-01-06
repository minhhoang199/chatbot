package com.example.chatwebproject.service;

import com.example.chatwebproject.constant.DomainCode;
import com.example.chatwebproject.exception.ValidationRequestException;
import com.example.chatwebproject.model.dto.MessageEditHistoryDto;
import com.example.chatwebproject.model.entity.MessageEditHistory;
import com.example.chatwebproject.repository.MessageEditHistoryRepository;
import com.example.chatwebproject.repository.MessageRepository;
import com.example.chatwebproject.transformer.MessageEditHistoryTransformer;
import com.example.chatwebproject.transformer.MessageTransformer;
import com.example.chatwebproject.utils.SecurityUtil;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Repository;
import org.springframework.util.CollectionUtils;

import javax.transaction.Transactional;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

@Repository
@RequiredArgsConstructor
@Slf4j
public class MessageEditHistoryService {
    private final MessageEditHistoryRepository messageEditHistoryRepository;
    private final MessageRepository messageRepository;
    @Transactional
    public MessageEditHistoryDto save(String content, Long messageId) {
        MessageEditHistory messageEditHistory = MessageEditHistory.builder()
                .content(content)
                .messageId(messageId)
                .build();
        messageEditHistory = this.messageEditHistoryRepository.save(messageEditHistory);
        return MessageEditHistoryTransformer.toDto(messageEditHistory);
    }

    public List<MessageEditHistoryDto> getEditHistory(Long messageId) {
        List<Long> userIds = this.messageRepository.findUsersInRoomByMessageId(messageId);
        if (CollectionUtils.isEmpty(userIds) || !userIds.contains(SecurityUtil.getCurrentUserIdLogin())) {
            throw new ValidationRequestException(DomainCode.FORBIDDEN, new Object[]{"This user do not have permission"}, null);
        }
        List<MessageEditHistory> messageEditHistories = this.messageEditHistoryRepository.findByMessageId(messageId);
        if (CollectionUtils.isEmpty(messageEditHistories)) return new ArrayList<>();
        return messageEditHistories.stream().map(MessageEditHistoryTransformer::toDto).collect(Collectors.toList());
    }
}
