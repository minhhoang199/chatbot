package com.example.chatwebproject.model.entity;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
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
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.OneToMany;
import javax.persistence.Table;
import java.util.ArrayList;
import java.util.List;

@Entity
@Table(name = "attached_file")
@Data
@AllArgsConstructor
@NoArgsConstructor
public class AttachedFile extends BaseEntity {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne
    @JoinColumn(name = "message_id")
    @JsonIgnoreProperties(value = "attachedFiles")
    private Message message;

    @Column(name = "link_file")
    private String linkFile;

    @Column(name = "link_name")
    private String fileName;

    @Column(name = "extension")
    private String extension;
}
