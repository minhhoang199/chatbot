package com.example.chatwebproject.repository;

import com.example.chatwebproject.model.Connection;
import com.example.chatwebproject.model.enums.ConnectionStatus;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface ConnectionRepository extends JpaRepository<Connection, Long> {
    @Query("SELECT c FROM Connection c Where c.requestUser.email = ?1 " +
            "AND c.acceptedUser.email = ?2 " +
            "AND c.connectionStatus = ?3")
    Optional<Connection> findByUsersAndStatus(String followingEmail, String followedEmail, ConnectionStatus connectionStatus);

    @Query("SELECT c FROM Connection c Where c.requestUser.email = ?1 " +
            "AND c.acceptedUser.email = ?2")
    Optional<Connection> findByUsers(String followingEmail, String followedEmail);
}
