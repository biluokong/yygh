package com.biluo.yygh.oss.controller;

import com.biluo.yygh.common.result.Result;
import com.biluo.yygh.oss.service.FileService;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;

@RestController
@RequestMapping("/api/oss/file")
@RequiredArgsConstructor
public class FileApiController {
    private final FileService fileService;

    //上传文件到阿里云oss
    @PostMapping("fileUpload")
    public Result<String> fileUpload(MultipartFile file) {
        //获取上传文件
        String url = fileService.upload(file);
        return Result.ok(url);
    }
}
