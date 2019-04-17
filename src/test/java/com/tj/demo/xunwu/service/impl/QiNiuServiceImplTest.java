package com.tj.demo.xunwu.service.impl;

import com.qiniu.common.QiniuException;
import com.qiniu.http.Response;
import com.tj.demo.xunwu.service.IQiNiuService;
import org.junit.Assert;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.junit4.SpringRunner;

import javax.validation.constraints.AssertTrue;
import java.io.File;

import static org.junit.Assert.*;
@RunWith(SpringRunner.class)
@SpringBootTest
public class QiNiuServiceImplTest {

    @Autowired
    private IQiNiuService qiNiuService;
    @Test
    public void uplodadFile() {
        String fileName="F:\\project\\springBootProject\\xunwu\\tmp\\热部署失败idea需要调整的东西.png";
        File file=new File(fileName);
        try {
            Response response=qiNiuService.uplodadFile(file);
            Assert.assertTrue(response.isOK());
        } catch (QiniuException e) {
            e.printStackTrace();
        }
    }

    @Test
    public void uplodadFile1() {
    }

    @Test
    public void delete() {
        String key="Fl3xkwZ2DsBWVnwk0sPJuMuZgD02";
        try {
            Response response=qiNiuService.delete(key);
            Assert.assertTrue(response.isOK());
        } catch (QiniuException e) {
            e.printStackTrace();
        }
    }

    @Test
    public void afterPropertiesSet() {
    }
}