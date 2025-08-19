package com.example.chatwebproject.model.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Data
@Builder
public class AttachedFileDto {
    private Long id;

    private Long messageId;

    private String linkFile;

    private String fileName;

    private String linkPreview;

    private String extension;

    private Long roomId;
}
