package com.example.chatwebproject.model.entity;

import com.example.chatwebproject.model.enums.ConnectionStatus;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import javax.persistence.*;

@Entity
@Table(name = "connection")
@Data
@NoArgsConstructor
@AllArgsConstructor
public class Connection {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Enumerated(EnumType.STRING)
    @Column(name = "connection_status", nullable = false)
    private ConnectionStatus connectionStatus;

    @ManyToOne
    @JoinColumn(name = "following_user",  referencedColumnName = "email")
    @JsonIgnoreProperties(value = "messages", allowSetters = true)
    private User requestUser;

    @ManyToOne
    @JoinColumn(name = "followed_user", referencedColumnName = "email")
    @JsonIgnoreProperties(value = "messages", allowSetters = true)
    private User acceptedUser;

    private String accepted;

}
