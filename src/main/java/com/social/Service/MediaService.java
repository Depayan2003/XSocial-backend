package com.social.Service;

import com.social.DTO.MediaUploadResponse;
import org.springframework.web.multipart.MultipartFile;

public interface MediaService {
    MediaUploadResponse upload(MultipartFile file);
}
