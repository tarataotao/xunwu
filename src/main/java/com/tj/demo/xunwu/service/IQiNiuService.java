package com.tj.demo.xunwu.service;

import com.qiniu.common.QiniuException;
import com.qiniu.http.Response;

import java.io.File;
import java.io.InputStream;

/**
 * 七牛云服务
 */
public interface IQiNiuService {

    Response uplodadFile(File file) throws QiniuException;

    Response uplodadFile(InputStream inputStream) throws QiniuException;

    Response delete(String key) throws  QiniuException;

}
