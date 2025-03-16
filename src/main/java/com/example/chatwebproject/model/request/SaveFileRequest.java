package com.example.chatwebproject.model.request;

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
public class SaveFileRequest {
    private String linkFile;

    private String fileName;

    private String extension;

    private Long roomId;
}
