package com.example.chatwebproject.model.response;

import com.example.chatwebproject.model.dto.AttachedFileDto;
import lombok.Builder;
import lombok.Data;

@Data
@Builder
public class DownloadFileResponse {
    private AttachedFileDto fileDto;
    private byte[] bytes;
}
