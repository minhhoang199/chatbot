package com.example.chatwebproject.repository;

import com.example.chatwebproject.model.entity.MessageEditHistory;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface MessageEditHistoryRepository extends JpaRepository<MessageEditHistory, Long> {
    @Query("SELECT meh FROM MessageEditHistory meh WHERE meh.id = :id AND meh.delFlag = FALSE")
    Optional<MessageEditHistory> getByIdAndNotDel(@Param("id")Long id);

    @Query("SELECT meh FROM MessageEditHistory meh WHERE meh.messageId = :messageId AND meh.delFlag = FALSE")
    List<MessageEditHistory> findByMessageId(@Param("messageId")Long messageId);
}
