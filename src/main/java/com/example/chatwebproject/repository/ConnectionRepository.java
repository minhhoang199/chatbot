package com.example.chatwebproject.repository;

import com.example.chatwebproject.model.Connection;
import com.example.chatwebproject.model.enums.ConnectionStatus;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface ConnectionRepository extends JpaRepository<Connection, Long> {
    @Query("SELECT c FROM Connection c Where c.requestUser.phone = ?1 " +
            "AND c.acceptedUser.phone = ?2 " +
            "AND c.connectionStatus = ?3")
    Optional<Connection> findByUsersAndStatus(String followingPhone, String followedPhone, ConnectionStatus connectionStatus);

    @Query("SELECT c FROM Connection c Where c.requestUser.phone = ?1 " +
            "AND c.acceptedUser.phone = ?2")
    Optional<Connection> findByUsers(String followingPhone, String followedPhone);
}
