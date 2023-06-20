package com.example.chatwebproject.repository;

import com.example.chatwebproject.model.Conversation;
import com.example.chatwebproject.model.enums.ConversationType;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface ConversationRepository extends JpaRepository<Conversation, Long> {
    @Query("SELECT DISTINCT c FROM Conversation c INNER JOIN c.accounts u where u.id = ?1")
    List<Conversation> findByUserId(Long userId);

    @Query("SELECT DISTINCT  c FROM Conversation c INNER JOIN c.accounts u WHERE u.id = ?1 " +
            "AND c.name LIKE %?2%")
    List<Conversation> findByUserIdAndChatName(Long userId, String name);

    @Query("SELECT c FROM Conversation c INNER JOIN c.accounts u WHERE u.phone = ?1 " +
            "AND c.conversationType = ?2")
    List<Conversation> findByPhoneAndType(String phone, ConversationType conversationType);
}