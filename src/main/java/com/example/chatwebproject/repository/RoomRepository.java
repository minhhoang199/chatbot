package com.example.chatwebproject.repository;

import com.example.chatwebproject.model.Room;
import com.example.chatwebproject.model.RoomProjection;
import com.example.chatwebproject.model.enums.RoomType;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface RoomRepository extends JpaRepository<Room, Long> {
    @Query("SELECT c FROM Room c INNER JOIN c.users u where u.id = ?1")
    List<Room> findByUserId(Long userId);

    @Query("SELECT DISTINCT  c FROM Room c INNER JOIN c.users u WHERE u.id = ?1 " +
            "AND c.name LIKE %?2%")
    List<Room> findByUserIdAndChatName(Long userId, String name);

    @Query("SELECT c FROM Room c INNER JOIN c.users u WHERE u.email = ?1 " +
            "AND c.roomType = ?2")
    List<Room> findByEmailAndType(String email, RoomType roomType);

    @Query(value = sql, nativeQuery = true)
    List<RoomProjection> findByUserId2(Long userId);
    public final String sql = "Select r.id, r.name, r.conversation_type AS conversationType," +
            " r.last_message_content AS lastMessageContent, r.last_message_time AS lastMessageTime" +
            " from ROOM r " +
            " JOIN room_user ru on r.id = ru.room_id " +
            " JOIN user_info u on ru.user_id = u.id " +
            " WHERE u.id = :userId";

    @Query("SELECT DISTINCT  r FROM Room r WHERE r.privateKey IN :privateKeys AND r.roomType = 'PRIVATE_CHAT'")
    List<Room> findByPrivateKeyIn(@Param("privateKeys") List<String> privateKeys);
}