package com.example.chatwebproject.service;

import com.example.chatwebproject.constant.DomainCode;
import com.example.chatwebproject.exception.ChatApplicationException;
import com.example.chatwebproject.model.dto.AttachedFileDto;
import com.example.chatwebproject.model.response.DownloadFileResponse;
import com.example.chatwebproject.model.dto.MessageDto;
import com.example.chatwebproject.model.dto.RoomDto;
import com.example.chatwebproject.model.entity.AttachedFile;
import com.example.chatwebproject.model.enums.MessageType;
import com.example.chatwebproject.model.response.UploadFileInfoResponse;
import com.example.chatwebproject.repository.AttachedFileRepository;
import com.example.chatwebproject.service.minio.MinIOService;
import com.example.chatwebproject.transformer.AttachedFileTransformer;
import com.example.chatwebproject.utils.SecurityUtil;
import lombok.RequiredArgsConstructor;
import org.springframework.messaging.simp.SimpMessagingTemplate;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import javax.transaction.Transactional;

@Service
@RequiredArgsConstructor
public class AttachedFileService {
    private final AttachedFileRepository attachedFileRepository;
    private final MessageService messageService;
    private final MinIOService minIOService;
    private final RoomService roomService;
    private final SimpMessagingTemplate messagingTemplate;

    @Transactional
    public AttachedFileDto saveFile(Long roomId, MultipartFile file) {
        //validate roomId
        RoomDto roomDto = this.roomService.findRoomById(roomId);
        //add to minio
        UploadFileInfoResponse uploadFileInfoResponse = this.minIOService.uploadFileMinIO(file, roomId);
        try {
            AttachedFile savedFile = AttachedFileTransformer.toEntityFromResponseInfo(uploadFileInfoResponse, roomId);
            AttachedFile entity = this.attachedFileRepository.save(savedFile);

            MessageDto messageDto = this.messageService.saveMessage(MessageDto.builder()
                    .content(entity.getFileName())
                    .sender(SecurityUtil.getCurrentEmailLogin())
                    .type(MessageType.CHAT)
                    .roomId(roomId)
                    .attachedFile(AttachedFileDto.builder().id(entity.getId()).build())
                    .build());
            AttachedFileDto dto = AttachedFileTransformer.toDto(entity);
            dto.setLinkPreview(uploadFileInfoResponse.getLinkPreview());
            String destination = "/topic/room/" + roomId;
            messagingTemplate.convertAndSend(destination, messageDto);
            return dto;
        } catch (Exception e) {
            this.minIOService.deleteFile(uploadFileInfoResponse.getLinkFile());
            throw new ChatApplicationException(DomainCode.INTERNAL_SERVICE_ERROR, new Object[]{e.getMessage()});
        }
    }

    public DownloadFileResponse download(Long roomId, Long fileId) {
        try {
            // Get file from DB
            AttachedFile file = attachedFileRepository.findByRoomIdAndFileId(roomId, fileId).orElseThrow(
                    () -> new ChatApplicationException(DomainCode.INVALID_PARAMETER, new Object[]{"Not found file by id and roomId"})
            );
            AttachedFileDto fileDto = AttachedFileTransformer.toDto(file);
            byte[] bytes = this.minIOService.downloadFile(fileDto);
            return DownloadFileResponse.builder().fileDto(fileDto).bytes(bytes).build();
        } catch (Exception e) {
            throw new ChatApplicationException(DomainCode.DOWNLOAD_FILE_FAIL, new Object[]{e.getMessage()});
        }
    }
}
