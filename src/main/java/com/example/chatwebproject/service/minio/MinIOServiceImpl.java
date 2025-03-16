package com.example.chatwebproject.service.minio;



import com.example.chatwebproject.config.minio.MinIOProperties;
import com.example.chatwebproject.constant.Constants;
import com.example.chatwebproject.constant.DomainCode;
import com.example.chatwebproject.exception.ChatApplicationException;
import com.example.chatwebproject.model.response.UploadFileInfoResponse;
import com.example.chatwebproject.utils.CommonUtils;
import com.example.chatwebproject.utils.DateUtil;
import io.minio.CopyObjectArgs;
import io.minio.CopySource;
import io.minio.GetObjectArgs;
import io.minio.GetPresignedObjectUrlArgs;
import io.minio.MinioClient;
import io.minio.PutObjectArgs;
import io.minio.RemoveObjectArgs;
import io.minio.StatObjectArgs;
import io.minio.errors.ErrorResponseException;
import io.minio.errors.InsufficientDataException;
import io.minio.errors.InternalException;
import io.minio.errors.InvalidResponseException;
import io.minio.errors.ServerException;
import io.minio.errors.XmlParserException;
import io.minio.http.Method;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.io.FilenameUtils;
import org.apache.commons.lang3.StringUtils;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.security.InvalidKeyException;
import java.security.NoSuchAlgorithmException;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
import java.util.UUID;
import java.util.zip.ZipOutputStream;


@Service
@Slf4j
@RequiredArgsConstructor
@Transactional
public class MinIOServiceImpl implements MinIOService {
    private final String DRAFT_FILE_FOLDER = "draft_file";
    private final MinIOProperties minioProperties;
    private final MinioClient minioClient;

    @Transactional
    @Override
    public UploadFileInfoResponse uploadFileMinIO(MultipartFile file, Long roomId) {
        String contentType = getContentType(FilenameUtils.getExtension(file.getOriginalFilename()));

        if (StringUtils.isEmpty(contentType)) {
            log.debug("Content type is empty, contentType: {}", contentType);

            throw new ChatApplicationException(DomainCode.FILE_NOT_SUPPORT);
        }

        try {
            String fileId = UUID.randomUUID() + "_" + file.getOriginalFilename();
            String folder = Objects.toString(roomId, DRAFT_FILE_FOLDER);
            String path = minioProperties.getFilePathBase() + "/" + folder + "/" + DateUtil.getCurrentDate(Constants.YYYY_MM_DD_FORMAT) + fileId;

            InputStream dataFile = file.getInputStream();
            log.info("Path upload: {}", path);

            minioClient.putObject(PutObjectArgs.builder().stream(dataFile, dataFile.available(), -1).bucket(minioProperties.getBucket()).object(path).contentType(contentType).build());

            return UploadFileInfoResponse.builder().linkFile(path).linkPreview(genPresignLinkUpload(path)).fileName(file.getOriginalFilename())
                    .extension(CommonUtils.getFileExtension(file)).build();

        } catch (Exception e) {
            log.error("Error on upload file to MinIO with cause {}", e.getMessage());

            throw new ChatApplicationException(DomainCode.MINIO_UPLOAD_FAIL);
        }
    }

    @Override
    public String genPresignLinkUpload(String path) {
        try {
            return minioClient.getPresignedObjectUrl(GetPresignedObjectUrlArgs.builder().method(Method.GET).bucket(minioProperties.getBucket()).object(path).expiry(minioProperties.getExpiry()).build());
        } catch (Exception exception) {
            log.error("Error on genPresignLinkUpload to MinIO with cause {}", exception.getMessage());
            throw new ChatApplicationException(DomainCode.MINIO_GEN_LINK_FAIL);
        }
    }

    @Override
    public Boolean checkDocumentExist(String path) {
        String transferFolderName = "transfer";

        path = path.substring(path.indexOf(transferFolderName));

        String bucketName = minioProperties.getBucket();

        try {
            minioClient.statObject(StatObjectArgs.builder().bucket(bucketName).object(path).build());
            return true;
        } catch (ErrorResponseException e) {
            if ("NoSuchKey".equals(e.errorResponse().code())) {
                return false;
            } else {
                throw new RuntimeException("Error occurred while checking object existence", e);
            }
        } catch (InsufficientDataException | InvalidResponseException |
                XmlParserException | NoSuchAlgorithmException |
                InternalException | InvalidKeyException | IOException |
                ServerException e) {
            throw new RuntimeException("Error occurred while checking object existence", e);
        }
    }

    @Override
    public List<UploadFileInfoResponse> genLinkFileLogin() {
        List<UploadFileInfoResponse> responseList = new ArrayList<>();
        String bucket = "static";
        String pathBg = "keycloak-bg.jpg";
        String pathLogo = "keycloak-logo.png";
        String[] files = new String[]{pathBg, pathLogo};
        for (String file : files) {
            try {
                minioClient.getObject(GetObjectArgs.builder().bucket(bucket).object(file).build());
                String path = minioClient.getPresignedObjectUrl(GetPresignedObjectUrlArgs.builder().method(Method.GET).bucket(bucket).object(file).expiry(31536000).build());
                responseList.add(UploadFileInfoResponse.builder().fileName(file).linkPreview(path).build());
            } catch (Exception exception) {
                log.error("Error on genPresignLinkUpload to MinIO with cause {}", exception.getMessage());
                throw new ChatApplicationException(DomainCode.MINIO_GEN_LINK_FAIL);
            }
        }
        return responseList;
    }

    @Override
    public byte[] downloadFile(Long roomId, String action) {
        try {
            return downloadFolderAsZip(roomId, action).readAllBytes();
        } catch (Exception e) {
            log.info("downloadFile fail with cause: {}", e.getMessage());
            throw new ChatApplicationException(DomainCode.DOWNLOAD_FILE_FAIL);
        }
    }

    public ByteArrayInputStream downloadFolderAsZip(Long transferId, String action) throws IOException {
        ByteArrayOutputStream byteArrayOutputStream = new ByteArrayOutputStream();
        ZipOutputStream zipOutputStream = new ZipOutputStream(byteArrayOutputStream);

        // List all the files inside the folder
//        List<AttachedFile> files = attachedFileRepository.findAllByTransferId(transferId);
//        // For each file in the folder, add it to the zip file
//        for (AttachedFile file : files) {
//            InputStream fileStream = getFileFromMinio(file.getPath());
//            zipOutputStream.putNextEntry(new ZipEntry(file.getPath()));
//            IOUtils.copy(fileStream, zipOutputStream);
//            zipOutputStream.closeEntry();
//        }

        zipOutputStream.close();
        return new ByteArrayInputStream(byteArrayOutputStream.toByteArray());
    }

    private InputStream getFileFromMinio(String fileName) {
        try {
            // Retrieve the file from MinIO
            GetObjectArgs getObjectArgs = GetObjectArgs.builder().bucket(minioProperties.getBucket()).object(fileName).build();
            return minioClient.getObject(getObjectArgs);
        } catch (Exception exception) {
            log.info("getFileFromMinio fail with cause: {}", exception.getMessage());
            throw new ChatApplicationException(DomainCode.DOWNLOAD_FILE_FAIL);
        }
    }

    private String getContentType(String extensionFile) {
        if (StringUtils.isEmpty(extensionFile))
            throw new ChatApplicationException(DomainCode.INVALID_PARAMETER);
        extensionFile = extensionFile.toLowerCase();

        if ("jpg".equals(extensionFile) || "jpeg".equals(extensionFile))
            return "image/jpeg";

        if ("png".equals(extensionFile)) return "image/png";

        if ("pdf".equals(extensionFile)) return "application/pdf";

        if ("txt".equals(extensionFile)) return "text/plain";

        if ("zip".equals(extensionFile)) return "application/zip";

        if ("7z".equals(extensionFile)) return "application/x-7z-compressed";

        if ("rar".equals(extensionFile)) return "application/x-rar-compressed";

        if ("pptx".equals(extensionFile))
            return "application/vnd.openxmlformats-officedocument.presentationml.presentation";

        if ("ppt".equals(extensionFile)) return "application/vnd.ms-powerpoint";

        if ("doc".equals(extensionFile)) return "application/msword";

        if ("xls".equals(extensionFile)) return "application/vnd.ms-excel";

        if ("docx".equals(extensionFile))
            return "application/vnd.openxmlformats-officedocument.wordprocessingml.document";

        if ("xlsx".equals(extensionFile))
            return "application/vnd.openxmlformats-officedocument.spreadsheetml.sheet";

        if ("msg".equals(extensionFile))
            return "application/vnd.ms-outlook";

        return null;
    }

    @Override
    public String getBucketName() {
        return this.minioProperties.getBucket();
    }

    @Override
    public UploadFileInfoResponse moveDraftFileToTransferRequestFolder(String sourcePath, Long roomId, String fileType){
        String bucketName = this.getBucketName();
        String[] pathObjects = sourcePath.split("/");
        String minIOFileName = pathObjects[pathObjects.length - 1];
        String targetPath = minioProperties.getFilePathBase() + "/" + roomId + "/" + fileType + "/" + minIOFileName;
        try {
            //move from draft to roomId folder
            minioClient.copyObject(
                    CopyObjectArgs.builder()
                            .bucket(bucketName)
                            .object(targetPath)
                            .source(CopySource.builder()
                                    .bucket(bucketName)
                                    .object(sourcePath)
                                    .build())
                            .build()
            );

            // Delete the draft object
            minioClient.removeObject(
                    RemoveObjectArgs.builder()
                            .bucket(bucketName)
                            .object(sourcePath)
                            .build()
            );
            return UploadFileInfoResponse.builder().linkFile(targetPath).linkPreview(genPresignLinkUpload(targetPath))
                    .extension(minIOFileName.substring(minIOFileName.lastIndexOf(".") + 1)).build();
        } catch (Exception e) {
            throw new ChatApplicationException(DomainCode.INVALID_PARAMETER, new Object[]{"Move file from draft folder failed: " + sourcePath});
        }
    }

    @Override
    public void deleteFile(String objectName) {
        try {
            minioClient.removeObject(
                    RemoveObjectArgs.builder()
                            .bucket(this.getBucketName())
                            .object(objectName)
                            .build()
            );
            log.info("File deleted successfully: {}", objectName);
        } catch (Exception e) {
            throw new ChatApplicationException(DomainCode.INTERNAL_SERVICE_ERROR, new Object[]{"Error occurred while deleting file MinIO: " + e.getMessage()});
        }
    }
}
