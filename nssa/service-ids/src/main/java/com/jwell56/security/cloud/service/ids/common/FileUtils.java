package com.jwell56.security.cloud.service.ids.common;

import java.io.File;
import java.io.IOException;

import org.springframework.web.multipart.MultipartFile;

import lombok.extern.log4j.Log4j2;

@Log4j2
public class FileUtils {
	
	/**
	 * 上传文件
	 * @param file
	 * @param wordContentsId
	 */
	public static final void upload(MultipartFile file, String fileName ,String filepath) {
		if (file.isEmpty()) log.info("上传失败，请选择文件");
        File filePath = new File(filepath);
        

        if(!filePath.exists())filePath.mkdirs();
        try {
            file.transferTo(new File(filePath + "/" + fileName));
            log.info("上传成功");
        } catch (IOException e) {
            e.printStackTrace();
        }
	}
	
//	/**
//	 * 上传文件
//	 * @param file
//	 * @param wordContentsId
//	 */
//	public static final void upload1(MultipartFile file, String fileName) {
//		if (file.isEmpty()) log.info("上传失败，请选择文件");
//        File filePath = new File("/opt/webstored/license");
//        
//
//        if(!filePath.exists())filePath.mkdirs();
//        try {
//            file.transferTo(new File(filePath + "/" + fileName));
//            log.info("上传成功");
//        } catch (IOException e) {
//            e.printStackTrace();
//        }
//	}
	
}
