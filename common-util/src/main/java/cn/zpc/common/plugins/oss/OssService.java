package cn.zpc.common.plugins.oss;

import java.io.File;
import java.net.URL;
import java.util.Date;

import com.aliyun.oss.ClientException;
import com.aliyun.oss.OSSClient;
import com.aliyun.oss.OSSException;
import com.aliyun.oss.model.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import sun.misc.BASE64Encoder;

/**
 * Description:阿里云对象服务
 * Author: sukai
 * Date: 2017-08-18
 */
@Service
public class OssService {

    private final OSSClient client;
    private String bucketName;


    @Autowired
    public OssService(@Value("${aliyun.bucketName}") String bucketName, OSSClient client){
        this.bucketName = bucketName;
        this.client = client;
        try {
            if (!client.doesBucketExist(bucketName)) {
                System.out.println("Creating bucket " + bucketName + "\n");
                client.createBucket(bucketName);
                CreateBucketRequest createBucketRequest= new CreateBucketRequest(bucketName);
                createBucketRequest.setCannedACL(CannedAccessControlList.Default);
                client.createBucket(createBucketRequest);
            }
        } catch (ClientException ce) {
            System.out.println("Caught an ClientException, which means the client encountered "
                    + "a serious internal problem while trying to communicate with OSS, "
                    + "such as not being able to access the network.");
            System.out.println("Error Message: " + ce.getMessage());
        }
    }


    /**
     * 检测对象是否存在
     * @param key
     * @return
     */
    public boolean checkFileKeyExists(String key){
        return client.doesObjectExist(bucketName, key);
    }


    /**
     * 上传图片，返回缩略图链接
     * @param key
     * @param file
     * @return
     */
    public String putFile(String key, File file){
        try {
            client.putObject(new PutObjectRequest(bucketName, key, file));
            client.setObjectAcl(bucketName, key, CannedAccessControlList.Default);
        } catch (OSSException oe) {
            System.out.println("Caught an OSSException, which means your request made it to OSS, "
                    + "but was rejected with an error response for some reason.");
            System.out.println("Error Message: " + oe.getErrorCode());
            System.out.println("Error UserVerifyCode:       " + oe.getErrorCode());
            System.out.println("Request ID:      " + oe.getRequestId());
            System.out.println("Host ID:           " + oe.getHostId());
        }
        return getSimpleUrl(key);
    }


    /**
     * 获取图片的缩略图链接
     * @param key
     * @return
     */
    public String getSimpleUrl(String key){
        String style = "image/resize,m_fill,w_1024,h_576,limit_1/auto-orient,0/quality,q_70/format,jpg/interlace,1";

        GeneratePresignedUrlRequest request = new GeneratePresignedUrlRequest(bucketName, key);
        request.setProcess(style);
        request.setExpiration(new Date(System.currentTimeMillis() + 1000L * 60L * 60L * 24L * 15L));
        URL url = client.generatePresignedUrl(request);

        return url.toString();
    }


    /**
     * 获取带水印的缩略图链接
     * @param key
     * @return
     */
    public String getSimpleWaterMarkUrl(String key){

        String encodeObject = new BASE64Encoder().encode("water/watermark.png?x-oss-process=image/resize,P_15".getBytes());
        String style = "image/resize,m_fill,w_1024,h_576,limit_1/auto-orient,0/quality,q_70/format,jpg" +
                "/watermark,t_100,g_se,x_10,y_10,image_" + encodeObject + "/interlace,1";
        return getUrl(style, key);
    }


    /**
     * 获取原图链接
     * @param key
     * @return
     */
    public String getOriginUrl(String key){
        String style = "image/resize,m_fill,w_1024,h_576,limit_1/interlace,1";
        GeneratePresignedUrlRequest request = new GeneratePresignedUrlRequest(bucketName, key);
        request.setProcess(style);
        request.setExpiration(new Date(System.currentTimeMillis() + 1000L * 60L * 60L * 24L * 15L));
        URL url = client.generatePresignedUrl(request);
        return url.toString();
    }


    /**
     * 获取带水印原图链接
     * @param key
     * @return
     */
    public String getOriginWaterMarkUrl(String key){
        String encode = new BASE64Encoder().encode("water/watermark.png?x-oss-process=image/resize,P_15".getBytes());
        String style = "image/resize,m_fill,w_1024,h_576,limit_1/watermark,t_100,g_se,x_10,y_10,image_"
                + encode + "/interlace,1";
        return getUrl(style, key);
    }


    /**
     * 获取文件的下载地址
     * @param key
     * @return
     */
    public String getFileUrl(String key){
        GeneratePresignedUrlRequest request = new GeneratePresignedUrlRequest(bucketName, key);
        request.setExpiration(new Date(System.currentTimeMillis() + 1000L * 60L * 60L * 24L * 15L));
        URL url = client.generatePresignedUrl(request);
        return url.toString();
    }


    /**
     * 获取自定义处理方式的链接
     * @param style
     * @param key
     * @return
     */
    private String getUrl(String style, String key){
        GeneratePresignedUrlRequest request = new GeneratePresignedUrlRequest(bucketName, key);
        request.setProcess(style);
        request.setExpiration(new Date(System.currentTimeMillis() + 1000L * 60L * 60L * 24L * 15L));
        URL url = client.generatePresignedUrl(request);
        return url.toString();
    }

    /**
     * 获取小程序图片处理链接
     * @param key
     * @return
     */
    public String getMicroAppUrl(String key){
        ObjectMetadata metadata = client.getObjectMetadata(bucketName, key);
        Long fileSize = metadata.getContentLength();
        if(fileSize >= 24576){
            Integer scale = (int)(Math.floor(24576L * 100) / fileSize) + 1;
            String style = "image/resize,m_fill,w_320,h_256,limit_1/auto-orient,0/format,jpg/quality,q_" + scale + "/interlace,1";
            return getUrl(style, key);
        }else{
            return getSimpleUrl(key);
        }
    }

}
