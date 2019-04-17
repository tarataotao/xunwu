package com.tj.demo.xunwu.service.impl;

import com.qiniu.common.QiniuException;
import com.qiniu.http.Response;
import com.qiniu.storage.BucketManager;
import com.qiniu.storage.UploadManager;
import com.qiniu.util.Auth;
import com.qiniu.util.StringMap;
import com.tj.demo.xunwu.service.IQiNiuService;
import org.springframework.beans.factory.InitializingBean;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import java.io.File;
import java.io.InputStream;

@Service
public class QiNiuServiceImpl implements IQiNiuService,InitializingBean {

    @Autowired
    private UploadManager uploadManager;

    @Autowired
    private BucketManager bucketManager;

    @Autowired
    private Auth auth;

    @Value("${qiniu.Bucket}")
    private String bucket;

    private StringMap putPolicy;

    @Override
    public Response uplodadFile(File file) throws QiniuException {
        Response response=this.uploadManager.put(file,null,getUploadToken());
        int retry=0;
        if(response.needRetry() && retry<3){
            response=this.uploadManager.put(file,null,getUploadToken());
            retry++;
        }
        return response;
    }

    @Override
    public Response uplodadFile(InputStream inputStream) throws QiniuException {
        Response response=this.uploadManager.put(inputStream,null,getUploadToken(),putPolicy,null);
        int retry=0;
        if(response.needRetry() && retry<3){
            response=this.uploadManager.put(inputStream,null,getUploadToken(),putPolicy,null);
            retry++;
        }
        return response;
    }

    @Override
    public Response delete(String key) throws QiniuException {
        Response response=this.bucketManager.delete(this.bucket,key);

        int retry=0;
        if(response.needRetry() && retry<3){
            response=this.bucketManager.delete(this.bucket,key);
            retry++;
        }
        return response;
    }

    @Override
    public void afterPropertiesSet() throws Exception {
        putPolicy=new StringMap();
        putPolicy.put("returnBody", "{\"key\":\"$(key)\",\"hash\":\"$(etag)\",\"bucket\":\"$(bucket)\",\"width\":$(imageInfo.width), \"height\":${imageInfo.height}}");
    }

    /**
     * 获取上传凭证
     * @return
     */
    private String getUploadToken(){
        return this.auth.uploadToken(bucket,null,3600,putPolicy);
    }
}
