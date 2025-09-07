package com.example.chatwebproject.model.entity;

import com.example.chatwebproject.model.enums.FriendshipStatus;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import javax.persistence.*;

@Entity
@Table(name = "friend_ship")
@Data
@NoArgsConstructor
@AllArgsConstructor
public class Friendship extends BaseEntity {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Enumerated(EnumType.STRING)
    @Column(name = "connection_status", nullable = false)
    private FriendshipStatus status;

    private String requestUserEmail;

    private String acceptedUserEmail;

    private String blockUserEmail;

}
