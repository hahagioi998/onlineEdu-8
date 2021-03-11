package com.online.edu.service.oss.service;

import java.io.InputStream;

public interface FileService {

    /**
     *  阿里云oss文件上传  图片上传至阿里云
     * @param inputStream    从浏览器上传文件都会是InputStream  输入流
     * @param module         文件（头像）要上传的目录 文件夹名称  avatar
     * @param originalFilename  原始文件名称
     * @return   文件在oss服务器上的url地址
     */
    String upload(InputStream inputStream,String module, String originalFilename);

    /**
     * 阿里云文件删除
     * @param url  文件url地址，可以从数据库中拿到
     */
    void removeFile(String url);

}
