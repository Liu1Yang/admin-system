package com.liuyang.admin.vo;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

@Data
@Schema(description = "文件上传响应")
public class FileUploadVO {

    @Schema(description = "原始文件名")
    private String originalFilename;

    @Schema(description = "访问 URL")
    private String url;
}
