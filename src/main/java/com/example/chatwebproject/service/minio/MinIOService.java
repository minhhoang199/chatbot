package com.example.chatwebproject.service.minio;


import com.example.chatwebproject.model.dto.AttachedFileDto;
import com.example.chatwebproject.model.response.UploadFileInfoResponse;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.util.List;

public interface MinIOService {
    UploadFileInfoResponse uploadFileMinIO(MultipartFile file, Long roomId);

    String genPresignLinkUpload(String path);

    Boolean checkDocumentExist(String objectName);

    List<UploadFileInfoResponse> genLinkFileLogin();

    byte[] downloadFile(AttachedFileDto fileDto) throws IOException;

    void deleteFile(String objectName);

    String getBucketName();

    UploadFileInfoResponse moveDraftFileToTransferRequestFolder(String sourcePath, Long roomId, String fileType);
}

