package com.example.chatwebproject.model.response;

import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.experimental.FieldDefaults;

@Builder
@Data
@AllArgsConstructor
@NoArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE)
public class UploadFileInfoResponse {
    private String linkFile;
    private String linkPreview;
    private String fileName;
//    private String checklistCode;
    private String extension;
}

