package com.example.chatwebproject.service.minio;


import com.example.chatwebproject.model.response.UploadFileInfoResponse;
import org.springframework.web.multipart.MultipartFile;

import java.util.List;

public interface MinIOService {
    UploadFileInfoResponse uploadFileMinIO(MultipartFile file, Long roomId);

    String genPresignLinkUpload(String path);

    Boolean checkDocumentExist(String objectName);

    List<UploadFileInfoResponse> genLinkFileLogin();

    byte[] downloadFile(Long roomId, String action);

    void deleteFile(String objectName);

    String getBucketName();

    UploadFileInfoResponse moveDraftFileToTransferRequestFolder(String sourcePath, Long roomId, String fileType);
}

