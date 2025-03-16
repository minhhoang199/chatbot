package com.example.chatwebproject.service;

import com.example.chatwebproject.constant.DomainCode;
import com.example.chatwebproject.exception.ChatApplicationException;
import com.example.chatwebproject.model.dto.AttachedFileDto;
import com.example.chatwebproject.model.dto.RoomDto;
import com.example.chatwebproject.model.entity.AttachedFile;
import com.example.chatwebproject.model.request.SaveFileRequest;
import com.example.chatwebproject.model.response.UploadFileInfoResponse;
import com.example.chatwebproject.repository.AttachedFileRepository;
import com.example.chatwebproject.service.minio.MinIOService;
import com.example.chatwebproject.transformer.AttachedFileTransformer;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import javax.transaction.Transactional;

@Service
@RequiredArgsConstructor
public class AttachedFileService {
    private final AttachedFileRepository attachedFileRepository;
    private final MinIOService minIOService;
    private final RoomService roomService;

    @Transactional
    public AttachedFileDto saveFile(Long roomId, MultipartFile file) {
        //validate roomId
        RoomDto roomDto = this.roomService.findRoomById(roomId);
        //add to minio
        UploadFileInfoResponse uploadFileInfoResponse = this.minIOService.uploadFileMinIO(file, roomId);
        try {
            AttachedFile entity = AttachedFileTransformer.toEntityFromResponseInfo(uploadFileInfoResponse);
            AttachedFile savedFile = this.attachedFileRepository.save(entity);
            AttachedFileDto dto = AttachedFileTransformer.toDto(savedFile);
            dto.setLinkPreview(uploadFileInfoResponse.getLinkPreview());
            return dto;
        } catch (Exception e) {
            this.minIOService.deleteFile(uploadFileInfoResponse.getLinkFile());
            throw new ChatApplicationException(DomainCode.INTERNAL_SERVICE_ERROR, new Object[]{e.getMessage()});
        }
    }
}
