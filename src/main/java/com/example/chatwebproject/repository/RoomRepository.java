package com.example.chatwebproject.repository;

import com.example.chatwebproject.model.entity.Room;
import com.example.chatwebproject.model.entity.RoomProjection;
import com.example.chatwebproject.model.enums.RoomType;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface RoomRepository extends JpaRepository<Room, Long> {
    @Query("SELECT DISTINCT  c FROM Room c INNER JOIN c.users u WHERE u.id = ?1 " +
            "AND c.name LIKE %?2%")
    List<Room> findByUserIdAndChatName(Long userId, String name);

    @Query("SELECT r FROM Room r INNER JOIN r.users u" +
            " WHERE u.email IN (:email1, :email2) " +
            " AND r.roomType = :roomType " +
            " AND r.delFlag = false " +
            " GROUP BY r.id " +
            " HAVING COUNT(DISTINCT u.email) = 2 ")
    List<Room> findByEmailAndType(@Param("email1")String email1,
                                  @Param("email2")String email2,
                                  @Param("roomType")RoomType roomType);

    @Query(value = sql, nativeQuery = true)
    List<RoomProjection> findByUserId(Long userId);
    public final String sql = "Select r.id, r.name, r.conversation_type AS conversationType," +
            " r.last_message_content AS lastMessageContent, r.last_message_time AS lastMessageTime," +
            " r.status AS status " +
            " from ROOM r " +
            " JOIN room_user ru on r.id = ru.room_id " +
            " JOIN user_info u on ru.user_id = u.id " +
            " WHERE u.id = :userId " +
            " AND r.del_flag = false " +
            " ORDER BY r.last_message_time DESC ";

    @Query("SELECT DISTINCT  r FROM Room r WHERE r.privateKey IN :privateKeys AND r.roomType = 'PRIVATE_CHAT'")
    List<Room> findByPrivateKeyIn(@Param("privateKeys") List<String> privateKeys);

    @Query("SELECT c FROM Room c " +
            " where c.id = :roomId " +
            " AND (c.delFlag IS NULL OR c.delFlag = false)")
    Optional<Room> findByIdAndDelFlag(@Param("roomId") Long roomId);
}