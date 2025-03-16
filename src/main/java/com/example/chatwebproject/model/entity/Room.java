package com.example.chatwebproject.model.entity;

import com.example.chatwebproject.model.enums.RoomStatus;
import com.example.chatwebproject.model.enums.RoomType;
import com.fasterxml.jackson.annotation.JsonIgnore;
import lombok.*;

import javax.persistence.*;
import java.io.Serializable;
import java.time.LocalDateTime;
import java.util.HashSet;
import java.util.Objects;
import java.util.Set;

@Entity
@Table(name = "room")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class Room extends BaseEntity implements Serializable {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "name", length = 50, nullable = false)
    private String name;

    @Column(name = "last_message_content", length = 2000)
    private String lastMessageContent;

    @Column(name = "last_message_time", length = 50)
    private LocalDateTime lastMessageTime;

    @Enumerated(EnumType.STRING)
    @Column(name = "room_status", nullable = false)
    private RoomStatus roomStatus;

    @Enumerated(EnumType.STRING)
    @Column(name = "conversation_type", nullable = false)
    private RoomType roomType;

    @Column(name = "admin")
    private String admin;

    @OneToMany(mappedBy = "room", fetch = FetchType.LAZY)
    @JsonIgnore
    private Set<Message> messages = new HashSet<>();

    @ManyToMany(fetch = FetchType.LAZY)
    @JoinTable(name = "room_user",
            joinColumns = @JoinColumn(name = "room_id"),
            inverseJoinColumns = @JoinColumn(name = "user_id"))
    @JsonIgnore
    private Set<User> users = new HashSet<>();

    @Column(name = "private_key")
    private String privateKey;

    @Column(name = "created_by", length = 200)
    private String createdBy;

    @Column(name = "updated_by", length = 200)
    private String updatedBy;

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof Room)) return false;
        Room room = (Room) o;
        return Objects.equals(getId(), room.getId());
    }
}
