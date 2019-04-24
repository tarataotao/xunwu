package com.tj.demo.xunwu.service.impl;

import com.tj.demo.xunwu.form.RentSearch;
import com.tj.demo.xunwu.service.ISearchService;
import com.tj.demo.xunwu.service.ServiceMultResult;
import org.junit.Assert;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.junit4.SpringRunner;

import javax.persistence.criteria.CriteriaBuilder;
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
        searchService.index(targetHouse);
    }

    @Test
    public void remove() {
        Long targetHouse=21L;
       searchService.remove(targetHouse);
    }
    @Test
    public void testQuery(){
        RentSearch rentSearch=new RentSearch();
        rentSearch.setCityEnName("bj");
        rentSearch.setStart(0);
        rentSearch.setSize(10);
        ServiceMultResult<Integer> serviceMultResult=searchService.query(rentSearch);
        System.out.println(serviceMultResult.getTotal());
        Assert.assertEquals(2,serviceMultResult.getTotal());
    }
}