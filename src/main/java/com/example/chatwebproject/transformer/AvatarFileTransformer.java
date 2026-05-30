package com.example.chatwebproject.transformer;

import com.example.chatwebproject.model.dto.AttachedFileDto;
import com.example.chatwebproject.model.dto.AvatarFileDto;
import com.example.chatwebproject.model.entity.AttachedFile;
import com.example.chatwebproject.model.entity.AvatarFile;
import com.example.chatwebproject.model.response.UploadFileInfoResponse;

public class AvatarFileTransformer {
    public static AvatarFileDto toDto(AvatarFile entity){
        AvatarFileDto fileDto = new AvatarFileDto();
        fileDto.setId(entity.getId());
        fileDto.setExtension(entity.getExtension());
        fileDto.setLinkFile(entity.getLinkFile());
        fileDto.setFileName(entity.getFileName());
        fileDto.setUserId(entity.getUserId());
        fileDto.setMessageId(entity.getMessage() != null ? entity.getMessage().getId() : null);
        return fileDto;
    }

    public static AvatarFile toEntity(AvatarFileDto dto){
        AvatarFile attachedFile = new AvatarFile();
        attachedFile.setId(dto.getId());
        attachedFile.setExtension(dto.getExtension());
        attachedFile.setLinkFile(dto.getLinkFile());
        attachedFile.setFileName(dto.getFileName());
        attachedFile.setUserId(dto.getUserId());
        return attachedFile;
    }

    public static AvatarFile toEntityFromResponseInfo(UploadFileInfoResponse infoResponse, Long userId){
        AvatarFile attachedFile = new AvatarFile();
        attachedFile.setExtension(infoResponse.getExtension());
        attachedFile.setLinkFile(infoResponse.getLinkFile());
        attachedFile.setFileName(infoResponse.getFileName());
        attachedFile.setUserId(userId);
        return attachedFile;
    }
}
