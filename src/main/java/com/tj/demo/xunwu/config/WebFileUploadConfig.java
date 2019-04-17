package com.tj.demo.xunwu.config;

import com.google.gson.Gson;
import com.qiniu.common.Zone;
import com.qiniu.storage.BucketManager;
import com.qiniu.storage.UploadManager;
import com.qiniu.util.Auth;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class WebFileUploadConfig {

    /**
     * 华东机房
     * @return
     */
    @Bean
    public com.qiniu.storage.Configuration qiniuConfig(){
        return new com.qiniu.storage.Configuration(Zone.zone0());
    }

    /**
     * 构建一个七牛上传工具实例
     * @return
     */
    @Bean
    public UploadManager uploadManager(){
        return new UploadManager(qiniuConfig());
    }

    @Value("${qiniu.AccessKey}")
    private String accessKey;
    @Value("${qiniu.SecretKey}")
    private String secretKey;

    /**
     * 认证信息实例
     * @return
     */
    @Bean
    public Auth auth(){
        return Auth.create(accessKey,secretKey);
    }

    /**
     * 构建七牛空间管理是实例
     * @return
     */
    @Bean
    public BucketManager bucketManager(){
        return new BucketManager(auth(),qiniuConfig());
    }

    @Bean
    public Gson gson(){
        return new Gson();
    }
}
