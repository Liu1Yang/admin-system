package com.liuyang.admin.service;

import com.liuyang.admin.vo.FileUploadVO;
import org.springframework.web.multipart.MultipartFile;

public interface FileService {

    FileUploadVO upload(MultipartFile file);
}
