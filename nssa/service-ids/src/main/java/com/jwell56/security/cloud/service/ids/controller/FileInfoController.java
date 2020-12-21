package com.jwell56.security.cloud.service.ids.controller;

import com.jwell56.security.cloud.service.ids.entity.FileInfo;
import com.jwell56.security.cloud.service.ids.service.FileInfoService;
import io.swagger.annotations.Api;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

/**
 * @author wsg
 * @since 2020/12/2
 */
@RestController
@Api(tags = "安全检测->文件日志")
@RequestMapping("/fileinfo")
public class FileInfoController extends IdsBaseController<FileInfo> {
    @Autowired
    public FileInfoController(FileInfoService service) {
        this.service = service;
    }
}
