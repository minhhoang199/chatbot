package com.example.chatwebproject.repository;


import com.example.chatwebproject.model.entity.Notification;
import com.example.chatwebproject.model.entity.Role;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface NotificationRepository extends JpaRepository<Notification, Long> {
}
