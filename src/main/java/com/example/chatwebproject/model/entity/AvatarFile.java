package com.example.chatwebproject.model.entity;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.OneToOne;
import javax.persistence.Table;

@Entity
@Table(name = "avatar_file")
@Data
@AllArgsConstructor
@NoArgsConstructor
public class AvatarFile extends BaseEntity {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @OneToOne (fetch = FetchType.LAZY)
    private Message message;

    @Column(name = "link_file")
    private String linkFile;

    @Column(name = "link_name")
    private String fileName;

    @Column(name = "extension")
    private String extension;

    @Column(name = "user_id")
    private Long userId;
}
