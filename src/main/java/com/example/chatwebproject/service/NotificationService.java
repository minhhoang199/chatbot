package com.example.chatwebproject.service;

import com.example.chatwebproject.model.dto.MessageDto;
import com.example.chatwebproject.repository.NotificationRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;
import java.time.Instant;
import java.util.List;

@Service
@RequiredArgsConstructor
@Slf4j
public class NotificationService {
    private final NotificationRepository notificationRepository;
    @PersistenceContext
    private final EntityManager entityManager;

    public List<MessageDto> getLimitNotificationByUserId(Instant before, Integer limit) {
        return null;
    }

    public Integer countByUserIdAndIsReadFalse() {
        return 0;
    }

    public String readNotification(Long id) {
        return null;
    }
}
