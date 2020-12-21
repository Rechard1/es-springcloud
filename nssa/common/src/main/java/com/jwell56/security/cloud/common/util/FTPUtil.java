package com.jwell56.security.cloud.common.util;

import sun.net.ftp.FtpClient;
import sun.net.ftp.FtpProtocolException;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.OutputStream;
import java.net.InetSocketAddress;
import java.net.SocketAddress;
import java.util.Arrays;
import java.util.List;

/**
 * @author wsg
 * @since 2019/4/14
 */
public class FTPUtil {
    private String ip;
    private String port;
    private String user;
    private String pass;
    private String path;

    public FTPUtil(String url, String user, String pass) {
        List<String> urlParamList = Arrays.asList(url.split("/"));
        List<String> ipPortList = Arrays.asList(urlParamList.get(2).split(":"));
        this.ip = ipPortList.get(0);
        this.port = ipPortList.get(1);
        this.path = "";
        for (int i = 3; i < urlParamList.size(); i++) {
            this.path += "/" + urlParamList.get(i);
        }
        this.path += "/";
        this.user=user;
        this.pass=pass;
    }

    public boolean isOk(){
        FtpClient ftp=connectFTP();
        return ftp.isLoggedIn();
    }

    private FtpClient connectFTP() {
        //创建ftp
        FtpClient ftp = null;
        try {
            //创建地址
            SocketAddress addr = new InetSocketAddress(this.ip, Integer.parseInt(this.port));
            //连接
            ftp = FtpClient.create();
            ftp.connect(addr);
            //登陆
            ftp.login(this.user, this.pass.toCharArray());
            ftp.setBinaryType();
        } catch (FtpProtocolException|IOException e) {
            e.printStackTrace();
        }
        return ftp;
    }

    public boolean upload(String localFile) {
        OutputStream os = null;
        FileInputStream fis = null;
        boolean flag=false;
        try {
            FtpClient ftp=connectFTP();
            File tempFile =new File(localFile.trim());
            String fileName = tempFile.getName();
            String ftpFile=this.path+fileName;
            // 将ftp文件加入输出流中。输出到ftp上
            os = ftp.putFileStream(ftpFile);
            File file = new File(localFile);
            // 创建一个缓冲区
            fis = new FileInputStream(file);
            byte[] bytes = new byte[1024];
            int c;
            while((c = fis.read(bytes)) != -1){
                os.write(bytes, 0, c);
            }
            flag=true;
        } catch (FtpProtocolException|IOException e) {
            e.printStackTrace();
        } finally {
            try {
                if(os!=null) {
                    os.close();
                }
                if(fis!=null) {
                    fis.close();
                }
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
        return flag;
    }
}
