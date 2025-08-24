package com.example.chatwebproject.repository;

import com.example.chatwebproject.model.entity.Message;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface MessageRepository extends JpaRepository<Message, Long> {
    @Query("SELECT m FROM Message m WHERE m.content LIKE %?1% AND m.delFlag = FALSE")
    List<Message> findByContentContaining(String content);

    @Query("SELECT m FROM Message m WHERE m.room.id = ?1 AND m.delFlag = FALSE ORDER BY m.createdAt")
    List<Message> findAllByRoomId(Long roomId);

    @Query("SELECT m FROM Message m WHERE m.id = :id AND m.delFlag = FALSE")
    Optional<Message> getByIdAndNotDel(@Param("id")Long id);

    @Query("SELECT m FROM Message m WHERE m.id = :messageId AND m.sender.id = :senderId AND m.delFlag = FALSE")
    Optional<Message> findByIdAndSender(@Param("messageId")Long messageId, @Param("senderId") Long senderId);
}
