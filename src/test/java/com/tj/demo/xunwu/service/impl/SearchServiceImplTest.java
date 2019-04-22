package com.tj.demo.xunwu.service.impl;

import com.tj.demo.xunwu.service.ISearchService;
import org.junit.Assert;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.junit4.SpringRunner;

import javax.validation.constraints.AssertTrue;

import static org.junit.Assert.*;

@RunWith(SpringRunner.class)
@SpringBootTest
public class SearchServiceImplTest {

    @Autowired
    private ISearchService searchService;
    @Test
    public void index() {
        Long targetHouse=21L;
        boolean success=searchService.index(targetHouse);
        Assert.assertTrue(success);
    }

    @Test
    public void remove() {
        Long targetHouse=21L;
        boolean success=searchService.remove(targetHouse);
        Assert.assertTrue(success);
    }
}