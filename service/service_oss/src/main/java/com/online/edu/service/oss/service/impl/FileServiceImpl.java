package com.online.edu.service.oss.service.impl;

import com.aliyun.oss.OSS;
import com.aliyun.oss.OSSClientBuilder;
import com.aliyun.oss.model.CannedAccessControlList;
import com.online.edu.service.oss.service.FileService;
import com.online.edu.service.oss.utils.OssProperties;
import org.joda.time.DateTime;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.io.FileInputStream;
import java.io.InputStream;
import java.util.UUID;

@Service
public class FileServiceImpl implements FileService {

    @Autowired
    private OssProperties ossProperties;

    // 文件上传 到 阿里云
    @Override
    public String upload(InputStream inputStream, String module, String originalFilename) {

        //读取配置信息
        String endpoint = ossProperties.getEndpoint();
        String keyid = ossProperties.getKeyid();
        String keysecret = ossProperties.getKeysecret();
        String bucketname = ossProperties.getBucketname();

        // 创建OSSClient实例。
        OSS ossClient = new OSSClientBuilder().build(endpoint, keyid, keysecret);

        //判断要上传的bucketname是否存在，不存在就创建，存在就直接使用
        boolean bucketExist = ossClient.doesBucketExist(bucketname);

        if(!bucketExist){
            //可以直接通过bucketname创建
            ossClient.createBucket(bucketname);
            //设置oss实例的访问权限：公共读
            ossClient.setBucketAcl(bucketname, CannedAccessControlList.PublicRead);
        }

        //构建文件访问路径 objectName  avatar/2020/04/15/default.jpg
        String folder = new DateTime().toString("yyyy/MM/dd");
        //这里 我们的文件名称不再为原来的名字。而是取出拓展名，然后给他一个随机的名字（用时间日期来命名），我们这里用UUID来命名
        String fileName = UUID.randomUUID().toString();
        //加上文件拓展名  lastIndexOf(".") 是从最后一个点开始到最后
        String fileExtension = originalFilename.substring(originalFilename.lastIndexOf("."));
        String key = module + "/" + folder + "/" + fileName + fileExtension;

        // 上传文件流。
        ossClient.putObject(bucketname, key, inputStream);

        // 关闭OSSClient。
        ossClient.shutdown();

        //返回uel地址
        // https://guli-file-191125.oss-cn-beijing.aliyuncs.com/avatar/default.jpg
        String url= "https://" + bucketname + "." + endpoint + "/"+ key;
        return url;
    }


    @Override
    public void removeFile(String url) {

        //读取配置信息
        String endpoint = ossProperties.getEndpoint();
        String keyid = ossProperties.getKeyid();
        String keysecret = ossProperties.getKeysecret();
        String bucketname = ossProperties.getBucketname();

        // 创建OSSClient实例。
        OSS ossClient = new OSSClientBuilder().build(endpoint, keyid, keysecret);

        // 阿里云存储的objectName文件名：avatar/2020/12/16 /4457c37777ea4d41bb8f83d1f44c00d4.jpg
        //阿里云中存储的url地址：https://onlinedu-file.oss-cn-beijing.aliyuncs.com/avatar/2020/12/16%20/4457c37777ea4d41bb8f83d1f44c00d4.jpg

        //去掉url中的https://onlinedu-file.oss-cn-beijing.aliyuncs.com/  就是objectName了
        String host = "https://" + bucketname + "." + endpoint + "/";
        // 截除数据
        String objectName = url.substring(host.length());
        // 删除文件。如需删除文件夹，请将ObjectName设置为对应的文件夹名称。如果文件夹非空，则需要将文件夹下的所有object删除后才能删除该文件夹。
        ossClient.deleteObject(bucketname, objectName);

        // 关闭OSSClient。
        ossClient.shutdown();
    }
}
