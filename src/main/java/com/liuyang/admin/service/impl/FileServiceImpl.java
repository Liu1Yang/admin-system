package com.liuyang.admin.service.impl;

import com.liuyang.admin.common.BusinessException;
import com.liuyang.admin.service.FileService;
import com.liuyang.admin.vo.FileUploadVO;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.Arrays;
import java.util.List;
import java.util.UUID;

@Service
public class FileServiceImpl implements FileService {

    private static final List<String> ALLOWED_EXTENSIONS = Arrays.asList("jpg", "jpeg", "png", "gif", "webp");

    @Value("${file.upload-dir:uploads}")
    private String uploadDir;

    @Override
    public FileUploadVO upload(MultipartFile file) {
        if (file == null || file.isEmpty()) {
            throw new BusinessException(400, "上传文件不能为空");
        }

        String originalFilename = file.getOriginalFilename();
        String extension = getExtension(originalFilename);
        if (!ALLOWED_EXTENSIONS.contains(extension)) {
            throw new BusinessException(400, "仅支持 jpg、jpeg、png、gif、webp 格式");
        }

        try {
            Path uploadPath = Paths.get(uploadDir).toAbsolutePath().normalize(); //确定要存到哪（路径标准化）
            Files.createDirectories(uploadPath); // 创建文件夹（如果不存在的话）

            String savedName = UUID.randomUUID().toString().replace("-", "") + "." + extension;//生成一个独一无二的文件名（防重名）
            Path targetPath = uploadPath.resolve(savedName); // 拼接完整的物理路径
            file.transferTo(targetPath.toFile()); // 把文件真正写到硬盘（执行传输）

            FileUploadVO vo = new FileUploadVO();
            vo.setOriginalFilename(originalFilename); // 用户上传时的原名
            vo.setUrl("/uploads/" + savedName);   // 浏览器访问地址
            return vo;
        } catch (IOException e) {// （比如磁盘满了、路径错误、文件被占用）
            throw new BusinessException(500, "文件上传失败");
        }
    }

    private String getExtension(String filename) {
        if (!StringUtils.hasText(filename) || !filename.contains(".")) {
            return "";
        }
        return filename.substring(filename.lastIndexOf('.') + 1).toLowerCase();
    }
}
