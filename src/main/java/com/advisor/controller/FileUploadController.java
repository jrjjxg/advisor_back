package com.advisor.controller;

import com.advisor.service.FileService;
import com.advisor.common.Result;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;

@RestController
@RequestMapping("/api/upload")
public class FileUploadController {

    private static final Logger log = LoggerFactory.getLogger(FileUploadController.class);

    @Autowired
    private FileService fileService;

    @PostMapping("/image")
    public Result<String> uploadImage(@RequestParam("file") MultipartFile file) throws IOException {
        String fileUrl = fileService.uploadFile(file);
        return Result.success(fileUrl);
    }

    @PostMapping("/avatar")
    public Result<String> uploadAvatar(@RequestParam("file") MultipartFile file) throws IOException {
        String fileUrl = fileService.uploadFile(file);
        return Result.success(fileUrl);
    }

    @PostMapping("/community")
    public Result<String> uploadCommunityImage(@RequestParam("file") MultipartFile file) throws IOException {
        try {
            log.info("接收到社区图片上传请求，文件名: {}, 大小: {}", file.getOriginalFilename(), file.getSize());
            String fileUrl = fileService.uploadFile(file);
            log.info("图片上传成功，七牛云返回URL: {}", fileUrl);
            return Result.success(fileUrl);
        } catch (IOException e) {
            log.error("上传社区图片失败", e);
            return Result.fail("上传失败：" + e.getMessage());
        }
    }

    @PostMapping("/journal")
    public Result<String> uploadJournalImage(@RequestParam("file") MultipartFile file) throws IOException {
        try {
            log.info("接收到日记图片上传请求，文件名: {}, 大小: {}", file.getOriginalFilename(), file.getSize());
            String fileUrl = fileService.uploadFile(file, "journal");
            log.info("日记图片上传成功，七牛云返回URL: {}", fileUrl);
            return Result.success(fileUrl);
        } catch (IOException e) {
            log.error("上传日记图片失败", e);
            return Result.fail("上传失败：" + e.getMessage());
        }
    }
    
    @PostMapping("/audio")
    public Result<String> uploadAudio(@RequestParam("file") MultipartFile file) throws IOException {
        try {
            log.info("接收到音频上传请求，文件名: {}, 大小: {}", file.getOriginalFilename(), file.getSize());
            String fileUrl = fileService.uploadFile(file, "voice");
            log.info("音频上传成功，七牛云返回URL: {}", fileUrl);
            return Result.success(fileUrl);
        } catch (IOException e) {
            log.error("上传音频失败", e);
            return Result.fail("上传失败：" + e.getMessage());
        }
    }
}