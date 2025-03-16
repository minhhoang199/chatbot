package com.example.chatwebproject.transformer;

import com.example.chatwebproject.model.dto.AttachedFileDto;
import com.example.chatwebproject.model.dto.MessageDto;
import com.example.chatwebproject.model.entity.AttachedFile;
import com.example.chatwebproject.model.entity.Message;
import com.example.chatwebproject.model.response.UploadFileInfoResponse;

public class AttachedFileTransformer {
    public static AttachedFileDto toDto(AttachedFile entity){
        AttachedFileDto fileDto = new AttachedFileDto();
        fileDto.setId(entity.getId());
        fileDto.setExtension(entity.getExtension());
        fileDto.setLinkFile(entity.getLinkFile());
        fileDto.setFileName(entity.getFileName());
        return fileDto;
    }

    public static AttachedFile toEntity(AttachedFileDto dto){
        AttachedFile attachedFile = new AttachedFile();
        attachedFile.setId(dto.getId());
        attachedFile.setExtension(dto.getExtension());
        attachedFile.setLinkFile(dto.getLinkFile());
        attachedFile.setFileName(dto.getFileName());
        return attachedFile;
    }

    public static AttachedFile toEntityFromResponseInfo(UploadFileInfoResponse infoResponse){
        AttachedFile attachedFile = new AttachedFile();
        attachedFile.setExtension(infoResponse.getExtension());
        attachedFile.setLinkFile(infoResponse.getLinkFile());
        attachedFile.setFileName(infoResponse.getFileName());
        return attachedFile;
    }
}
