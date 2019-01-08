package com.xianglin.act.common.util;

import com.aliyun.oss.OSSClient;
import com.aliyun.oss.model.CompleteMultipartUploadResult;
import com.aliyun.oss.model.UploadFileRequest;
import com.aliyun.oss.model.UploadFileResult;
import org.apache.commons.codec.digest.DigestUtils;
import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.ByteArrayInputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.InputStream;

/**
 * Created by wanglei on 2017/11/13.
 */
public class AliyunUtil {

    private static final Logger logger = LoggerFactory.getLogger(AliyunUtil.class);

    private static final String accessKeyId = "LTAIqBCrRyjR66Um";

    private static final String accessKeySecret = "v1RUDztVgatawBiTMOpyMHZB1VfBA6";

    private static String endpoint = "https://oss-cn-beijing.aliyuncs.com";

    private static final String SERVER_ALIAS = "https://cdn02.xianglin.cn/";

    private static String bucketName = "xianglin002";

    public static String uploadStream2Oss(InputStream is, String fileName) {

        OSSClient ossClient = new OSSClient(endpoint, accessKeyId, accessKeySecret);
        String location = "";
        try {
            //文件大小
            int fileSize = is.available();
            byte[] buffer = new byte[fileSize]; //流只能被读取一次，为了同时去MD5和上传流，必须先把流缓存，再重复用
            is.read(buffer);
            String stuffix = StringUtils.substringAfterLast(fileName, ".");
            String key = DigestUtils.md5Hex(buffer) + "-" + fileSize + "." + stuffix;
            ByteArrayInputStream byteArrayInputStream = new ByteArrayInputStream(buffer);
            ossClient.putObject(bucketName, key, byteArrayInputStream);
            location = SERVER_ALIAS + key;
        } catch (Exception e) {
            logger.error("上传阿里云OSS服务器异常." + e.getMessage(), e);
        }
        return location;
    }

    public static String uploadStream2Oss(byte[] buffer, String fileName) {
        OSSClient ossClient = new OSSClient(endpoint, accessKeyId, accessKeySecret);
        String location = "";
        try {
            String stuffix = StringUtils.substringAfterLast(fileName,".");
            String key = DigestUtils.md5Hex(buffer) + "-" + buffer.length + "." + stuffix;
            ByteArrayInputStream byteArrayInputStream = new ByteArrayInputStream(buffer);
            ossClient.putObject(bucketName, key, byteArrayInputStream);
            location = SERVER_ALIAS + key;
        } catch (Exception e) {
            logger.error("上传阿里云OSS服务器异常." + e.getMessage(), e);
        }
        return location;
    }



    public static String uploadSearchFile(File file) {
        String location = "";
        try {
            String bucketName = "xianglin002";
            String last = StringUtils.substringAfterLast(file.getName(), ".");
            String key = DigestUtils.md5Hex(new FileInputStream(file))+"-"+file.length() + "." + last;
            String endpoint = "https://oss-cn-beijing.aliyuncs.com";
            OSSClient ossClient = new OSSClient(endpoint, accessKeyId, accessKeySecret);
            UploadFileRequest uploadFileRequest = new UploadFileRequest(bucketName, key);
            uploadFileRequest.setUploadFile(file.getPath());
            uploadFileRequest.setTaskNum(5);
            uploadFileRequest.setEnableCheckpoint(true);
            UploadFileResult uploadResult = ossClient.uploadFile(uploadFileRequest);
            CompleteMultipartUploadResult multipartUploadResult = uploadResult.getMultipartUploadResult();
            location = StringUtils.replace(multipartUploadResult.getLocation(),"xianglin002.oss-cn-beijing.aliyuncs.com","cdn02.xianglin.cn") ;
            if(file.exists()){
                file.delete();
            }
        } catch (Throwable e) {
            logger.warn("AliyunUtil uploadImg ",e);
            throw new RuntimeException("上传阿里云oss失败");
        }
        return location;
    }


}
