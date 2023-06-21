package com.example.chatwebproject.repository;

import com.example.chatwebproject.model.Room;
import com.example.chatwebproject.model.enums.RoomType;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface RoomRepository extends JpaRepository<Room, Long> {
    @Query("SELECT DISTINCT c FROM Room c INNER JOIN c.accounts u where u.id = ?1")
    List<Room> findByUserId(Long userId);

    @Query("SELECT DISTINCT  c FROM Room c INNER JOIN c.accounts u WHERE u.id = ?1 " +
            "AND c.name LIKE %?2%")
    List<Room> findByUserIdAndChatName(Long userId, String name);

    @Query("SELECT c FROM Room c INNER JOIN c.accounts u WHERE u.phone = ?1 " +
            "AND c.roomType = ?2")
    List<Room> findByPhoneAndType(String phone, RoomType roomType);
}