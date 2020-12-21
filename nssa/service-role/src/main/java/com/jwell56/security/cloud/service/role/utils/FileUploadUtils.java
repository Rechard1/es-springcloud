package com.jwell56.security.cloud.service.role.utils;

import lombok.extern.slf4j.Slf4j;
import org.springframework.web.multipart.MultipartFile;

import javax.servlet.http.HttpServletResponse;
import java.io.*;
import java.math.BigInteger;
import java.security.MessageDigest;

@Slf4j
public class FileUploadUtils {

    /**
     * 上传文件
     */
    public static String upload(MultipartFile file) {
        File projectfile = new File(System.getProperty("user.dir")).getParentFile();
        String BASE_PATH = "/vx/";
        try {
            if (file.isEmpty()) {
                log.info("文件为空");
            }else{
                // 获取文件名
                String fileName = file.getOriginalFilename();
                // 设置文件存储路径
                String filePath = (new File(projectfile.getParent()+ BASE_PATH + fileName)).getAbsolutePath();
                File dest = new File(filePath);

                //上传路径检测
                String tempFile = (new File((new File(BASE_PATH + "test.txt")).getAbsolutePath())).getParent();
                String testFile = dest.getParent();
                if (!tempFile.equals(testFile)) {
                    log.info("上传路径错误");
                }

                // 检测是否存在目录
                if (!dest.getParentFile().exists()) {
                    dest.getParentFile().mkdirs();// 新建文件夹
                }

                file.transferTo(dest);// 文件写入

                return filePath;
            }
        }catch (IllegalStateException | IOException e) {
            e.printStackTrace();
        }
        return "";
    }

    public static void downloadFile(String fileName,String filePath,HttpServletResponse response){
        if (fileName != null && filePath != null) {
            //设置文件路径
            File file = new File(filePath);

            if (file.exists()) {
                response.setContentType("application/force-download");// 设置强制下载不打开
                response.addHeader("Content-Disposition", "form-data;name=attachment;fileName=" + fileName);// 设置文件名
                byte[] buffer = new byte[1024];
                FileInputStream fis = null;
                BufferedInputStream bis = null;
                boolean flag = false;
                try {
                    fis = new FileInputStream(file);
                    bis = new BufferedInputStream(fis);
                    OutputStream os = response.getOutputStream();
                    int i = bis.read(buffer);
                    while (i != -1) {
                        os.write(buffer, 0, i);
                        i = bis.read(buffer);
                    }
                    flag = true;
                } catch (Exception e) {
                    e.printStackTrace();
                    log.info("下载失败");
                } finally {
                    if (bis != null) {
                        try {
                            bis.close();
                        } catch (IOException e) {
                            e.printStackTrace();
                        }
                    }
                    if (fis != null) {
                        try {
                            fis.close();
                        } catch (IOException e) {
                            e.printStackTrace();
                        }
                    }
                }
                if (flag) {
                } else {
                    log.info("下载失败");
                }
            }
        }
    }

    public static String getFileMD5(File file) {
        if (!file.isFile()) {
            return null;
        }
        MessageDigest digest = null;
        FileInputStream in = null;
        byte buffer[] = new byte[8192];
        int len;
        try {
            digest = MessageDigest.getInstance("MD5");
            in = new FileInputStream(file);
            while ((len = in.read(buffer)) != -1) {
                digest.update(buffer, 0, len);
            }
            BigInteger bigInt = new BigInteger(1, digest.digest());
            return bigInt.toString(16);
        } catch (Exception e) {
            e.printStackTrace();
            return null;
        } finally {
            try {
                in.close();
            } catch (Exception e) {
                e.printStackTrace();
            }
        }

    }
}
