package com.example.chatwebproject.repository;

import com.example.chatwebproject.model.entity.Message;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

@Repository
public interface MessageRepository extends JpaRepository<Message, Long> {
    @Query("SELECT m FROM Message m WHERE m.content LIKE %?1% AND m.delFlag = FALSE")
    List<Message> findByContentContaining(String content);

    @Query("SELECT m FROM Message m WHERE m.room.id = :roomId " +
            " AND m.createdAt < :createdAt " +
            " AND m.delFlag = FALSE " +
            " ORDER BY m.createdAt DESC")
    List<Message> findAllByRoomId(@Param("roomId") Long roomId,
                                  @Param("createdAt") LocalDateTime createdAt,
                                  Pageable pageable);

    @Query("SELECT m FROM Message m WHERE m.room.id = :roomId " +
            " AND m.createdAt >= COALESCE(:fromDate, m.createdAt) " +
            " AND m.createdAt <= COALESCE(:toDate, m.createdAt) " +
            " AND m.delFlag = FALSE " +
            " ORDER BY m.createdAt DESC")
    List<Message> findAllByRoomIdFromTo(@Param("roomId") Long roomId,
                                        @Param("fromDate") LocalDateTime from,
                                        @Param("toDate") LocalDateTime to);

    @Query("SELECT m FROM Message m WHERE m.room.id = :roomId " +
            " AND m.content LIKE %:content%" +
            " AND m.delFlag = FALSE " +
            " ORDER BY m.createdAt DESC")
    List<Message> searchByContent(@Param("roomId") Long roomId,
                                  @Param("content") String content);

    @Query("SELECT m FROM Message m WHERE m.id = :id AND m.delFlag = FALSE")
    Optional<Message> getByIdAndNotDel(@Param("id")Long id);

    @Query("SELECT m FROM Message m WHERE m.id = :messageId AND m.sender.id = :senderId AND m.delFlag = FALSE")
    Optional<Message> findByIdAndSender(@Param("messageId")Long messageId, @Param("senderId") Long senderId);
}
