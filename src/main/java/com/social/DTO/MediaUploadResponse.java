package com.social.DTO;

import com.social.Model.Enums.MessageType;
import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
public class MediaUploadResponse {
    private String url;
    private MessageType messageType;
}
