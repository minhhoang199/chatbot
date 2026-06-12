package com.example.chatwebproject.service;

import com.example.chatwebproject.constant.DomainCode;
import com.example.chatwebproject.exception.ValidationRequestException;
import com.example.chatwebproject.model.dto.MessageDto;
import com.example.chatwebproject.model.dto.NotificationDto;
import com.example.chatwebproject.model.entity.Notification;
import com.example.chatwebproject.model.enums.NotificationType;
import com.example.chatwebproject.repository.NotificationRepository;
import com.example.chatwebproject.transformer.NotificationTransformer;
import com.example.chatwebproject.utils.SecurityUtil;
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

    public List<NotificationDto> getLimitNotificationByUserId(Instant before, Integer limit) {
        Long userId = SecurityUtil.getCurrentUserIdLogin();
        if (userId == null) {
            return List.of();
        }

        if (before == null) {
            before = Instant.now();
        }
        if (limit == null || limit <= 0) {
            limit = 20;
        }

        List<Notification> notifications = entityManager.createQuery(
                        "SELECT n FROM Notification n WHERE n.userId = :userId AND n.createdAt <= :before " +
                                " AND n.type <> :excludeType " +
                                " AND (n.delFlag IS NULL OR n.delFlag = false) ORDER BY n.createdAt DESC",
                        Notification.class)
                .setParameter("userId", userId)
                .setParameter("before", before)
                .setParameter("excludeType", NotificationType.MESSAGE_ADD)
                .setMaxResults(limit)
                .getResultList();

        return NotificationTransformer.toDtoList(notifications);
    }

    public Integer countByUserIdAndIsReadFalse() {
        Long userId = SecurityUtil.getCurrentUserIdLogin();
        if (userId == null) {
            return 0;
        }

        Long count = entityManager.createQuery(
                        "SELECT COUNT(n) FROM Notification n WHERE n.userId = :userId " +
                                "AND n.isRead = false AND n.type <> :excludeType " +
                                "AND (n.delFlag IS NULL OR n.delFlag = false)",
                        Long.class)
                .setParameter("userId", userId)
                .setParameter("excludeType", NotificationType.MESSAGE_ADD)
                .getSingleResult();

        return count != null ? count.intValue() : 0;
    }

    public String readNotification(Long id) {
        Notification notification = this.notificationRepository.findById(id)
                .orElseThrow(() -> new IllegalArgumentException("Notification not found with id: " + id));
        Long userId = SecurityUtil.getCurrentUserIdLogin();
        if (!notification.getUserId().equals(userId)) {
            throw new ValidationRequestException(DomainCode.INVALID_PARAMETER, new Object[]{"Invalid user"}, null);
        }
        notification.setIsRead(true);
        this.notificationRepository.save(notification);
        return "Notification marked as read";
    }

    public NotificationDto createNotification(NotificationDto notificationDto) {
        if (notificationDto == null) {
            throw new IllegalArgumentException("NotificationDto must not be null");
        }

        Long userId = notificationDto.getUserId();
        if (userId == null) {
            userId = SecurityUtil.getCurrentUserIdLogin();
        }
        if (userId == null) {
            throw new IllegalArgumentException("User id is required to create a notification");
        }

        Notification notification = new Notification();
        notification.setUserId(userId);
        notification.setMessageId(notificationDto.getMessageId());
        notification.setContent(notificationDto.getContent());
        notification.setType(notificationDto.getType());
        notification.setIsRead(Boolean.FALSE);
        notification.setDelFlag(false);

        Notification savedNotification = notificationRepository.save(notification);
        return NotificationTransformer.toDto(savedNotification);
    }

    public Integer countByUserIdAndIsReadFalseAndMessageAddType(Long roomId) {
        Long userId = SecurityUtil.getCurrentUserIdLogin();
        if (userId == null) {
            return 0;
        }

        if (roomId != null) {
            Long count = entityManager.createQuery(
                            "SELECT COUNT(n) FROM Notification n " +
                                    "WHERE n.userId = :userId AND n.isRead = false AND n.type = :type " +
                                    "AND n.messageId IN (SELECT m.id FROM Message m WHERE m.room.id = :roomId) " +
                                    "AND (n.delFlag IS NULL OR n.delFlag = false)",
                            Long.class)
                    .setParameter("userId", userId)
                    .setParameter("type", NotificationType.MESSAGE_ADD)
                    .setParameter("roomId", roomId)
                    .getSingleResult();
            return count != null ? count.intValue() : 0;
        } else {
            Long count = entityManager.createQuery(
                            "SELECT COUNT(n) FROM Notification n WHERE n.userId = :userId " +
                                    "AND n.isRead = false AND n.type = :type " +
                                    "AND (n.delFlag IS NULL OR n.delFlag = false)",
                            Long.class)
                    .setParameter("userId", userId)
                    .setParameter("type", NotificationType.MESSAGE_ADD)
                    .getSingleResult();
            return count != null ? count.intValue() : 0;
        }
    }
}
