package com.social.Service.Impl;

import com.cloudinary.Cloudinary;
import com.cloudinary.utils.ObjectUtils;
import com.social.DTO.MediaUploadResponse;
import com.social.Model.Enums.MessageType;
import com.social.Service.MediaService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.util.Map;

@Service
@RequiredArgsConstructor
public class MediaServiceImpl implements MediaService {

    private final Cloudinary cloudinary;

//    @Override
//    public MediaUploadResponse upload(MultipartFile file) {
//
//        try {
//            Map uploadResult = cloudinary.uploader().upload(
//                    file.getBytes(),
//                    ObjectUtils.asMap("resource_type", "auto")
//            );
//
//            String url = uploadResult.get("secure_url").toString();
//            String resourceType = uploadResult.get("resource_type").toString();
//
//            MessageType messageType = mapToMessageType(resourceType);
//
//            return new MediaUploadResponse(url, messageType);
//
//        } catch (Exception e) {
//            throw new RuntimeException("Media upload failed", e);
//        }
//    }
    
    @Override
    public MediaUploadResponse upload(MultipartFile file) {

        try {

            if (file.isEmpty()) {
                throw new RuntimeException("File is empty");
            }

            if (file.getSize() > 200 * 1024 * 1024) {
                throw new RuntimeException("File too large");
            }

            String contentType = file.getContentType();

            if (contentType == null ||
                    !(contentType.startsWith("image/")
                      || contentType.startsWith("video/")
                      || contentType.startsWith("audio/")
                      || contentType.equals("application/pdf")
                      || contentType.equals("application/vnd.openxmlformats-officedocument.wordprocessingml.document")
                      || contentType.equals("application/zip"))) {

                throw new RuntimeException("Unsupported file type");
            }

            Map uploadResult = cloudinary.uploader().upload(
            		file.getBytes(),
                    ObjectUtils.asMap(
                            "resource_type", "auto",
                            "use_filename", true,
                            "unique_filename", true
                    )
            );

            String url = uploadResult.get("secure_url").toString();

            MessageType messageType = mapToMessageType(contentType);

            return new MediaUploadResponse(url, messageType);

        } catch (Exception e) {
            throw new RuntimeException("Media upload failed", e);
        }
    }

//    private MessageType mapToMessageType(String resourceType) {
//        return switch (resourceType) {
//            case "image" -> MessageType.IMAGE;
//            case "video" -> MessageType.VIDEO; // audio also comes as video
//            case "raw" -> MessageType.FILE;
//            default -> MessageType.FILE;
//        };
//    }
    
    private MessageType mapToMessageType(String contentType) {

        if (contentType.startsWith("image/")) {
            return MessageType.IMAGE;
        }

        if (contentType.startsWith("video/")) {
            return MessageType.VIDEO;
        }

        if (contentType.startsWith("audio/")) {
            return MessageType.AUDIO;
        }

        if (contentType.equals("application/pdf")
                || contentType.equals("application/vnd.openxmlformats-officedocument.wordprocessingml.document")
                || contentType.equals("application/zip")) {

            return MessageType.FILE;
        }

        return MessageType.FILE;
    }


}
