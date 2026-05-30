package com.example.chatwebproject.service.minio;


import com.example.chatwebproject.model.dto.AttachedFileDto;
import com.example.chatwebproject.model.dto.AvatarFileDto;
import com.example.chatwebproject.model.entity.AvatarFile;
import com.example.chatwebproject.model.response.UploadFileInfoResponse;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.util.List;

public interface MinIOService {
    UploadFileInfoResponse uploadAttachedFileMinIO(MultipartFile file, Long roomId);

    UploadFileInfoResponse uploadAvatarFileMinIO(MultipartFile file, Long userId);

    String genPresignLinkUpload(String path);

    Boolean checkDocumentExist(String objectName);

    List<UploadFileInfoResponse> genLinkFileLogin();

    byte[] downloadAttachedFile(AttachedFileDto fileDto) throws IOException;

    byte[] downloadAvatarFile(AvatarFileDto fileDto) throws IOException;

    void deleteFile(String objectName);

    String getBucketName();

    UploadFileInfoResponse moveDraftFileToTransferRequestFolder(String sourcePath, Long roomId, String fileType);
}

