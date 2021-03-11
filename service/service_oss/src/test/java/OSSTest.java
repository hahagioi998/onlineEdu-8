import com.aliyun.oss.OSS;
import com.aliyun.oss.OSSClientBuilder;
import com.aliyun.oss.model.CreateBucketRequest;
import org.junit.jupiter.api.Test;

public class OSSTest {

    // Endpoint以杭州为例，其它Region请按实际情况填写。
    String endpoint = "http://oss-cn-beijing.aliyuncs.com";
    // 阿里云主账号AccessKey拥有所有API的访问权限，风险很高。
    // 强烈建议您创建并使用RAM账号进行API访问或日常运维，请登录 https://ram.console.aliyun.com 创建RAM账号。
    String accessKeyId = "LTAI4GEatxNfwcDfbEC4WvpK";
    String accessKeySecret = "gfKF4EA6zFyHidS2QxT4vBl4IP1qX2";
    String bucketName="onlinedu-file-2";

    // 创建存储空间bucketName
    @Test
    public void testCreateBucket(){

        // 创建OSSClient实例。
        OSS ossClient = new OSSClientBuilder().build(endpoint, accessKeyId, accessKeySecret);

        // 创建CreateBucketRequest对象。 创建的bucketName必须还没有存在
        CreateBucketRequest createBucketRequest = new CreateBucketRequest(bucketName);

        // 如果创建存储空间的同时需要指定存储类型以及数据容灾类型, 可以参考以下代码。
        // 此处以设置存储空间的存储类型为标准存储为例。
        // createBucketRequest.setStorageClass(StorageClass.Standard);
        // 默认情况下，数据容灾类型为本地冗余存储，即DataRedundancyType.LRS。如果需要设置数据容灾类型为同城冗余存储，请替换为DataRedundancyType.ZRS。
        // createBucketRequest.setDataRedundancyType(DataRedundancyType.ZRS)

        // 创建存储空间。
        ossClient.createBucket(createBucketRequest);

        // 关闭OSSClient。
        ossClient.shutdown();
    }


    // 判断bucketName是否已存在
    @Test
    public void testBucketNameExist(){
        // 创建OSSClient实例。
        OSS ossClient = new OSSClientBuilder().build(endpoint, accessKeyId, accessKeySecret);
        // 判断bucketName是否已经存在
        boolean exists = ossClient.doesBucketExist(bucketName);
        // 返回值为true，则代表已存在。false为未存在
        System.out.println(exists);
        //关闭ossClient
        ossClient.shutdown();
    }
}
