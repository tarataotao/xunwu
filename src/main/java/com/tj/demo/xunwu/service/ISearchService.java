package com.tj.demo.xunwu.service;

import com.tj.demo.xunwu.form.RentSearch;

import java.util.List;

/**
 * 检索接口
 */
public interface ISearchService {

    /**
     * 索引目标房源
     * @param houseId
     */
    void index(Long houseId);

    /**
     *移除房源索引
     * @param houseId
     */
    void remove(Long houseId);

    /**
     * 查询房源接口
     * @param rentSearch
     * @return
     */
    ServiceMultResult<Integer> query(RentSearch rentSearch);

    /**
     * 获取不全建议关键词
     * @param prefix
     * @return
     */
    ServiceResult<List<String>> suggest(String prefix);

}
