package com.example.chatwebproject.model.dto;

import com.example.chatwebproject.model.entity.BaseEntity;
import com.example.chatwebproject.model.enums.FriendshipStatus;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.EnumType;
import javax.persistence.Enumerated;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.Table;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class FriendshipDto extends BaseEntity {
    private Long id;

    private FriendshipStatus status;

    private String requestUserEmail;

    private String acceptedUserEmail;

    private String blockUserEmail;

}
