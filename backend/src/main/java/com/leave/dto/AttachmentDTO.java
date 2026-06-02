package com.leave.dto;

import lombok.Data;

@Data
public class AttachmentDTO {

    private Long id;
    private String fileName;
    private String filePath;
    private Long fileSize;
    private String fileType;
}
