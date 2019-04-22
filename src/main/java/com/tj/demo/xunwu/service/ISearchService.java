package com.tj.demo.xunwu.service;

/**
 * 检索接口
 */
public interface ISearchService {

    /**
     * 索引目标房源
     * @param houseId
     */
    boolean index(Long houseId);

    /**
     *移除房源索引
     * @param houseId
     */
    boolean remove(Long houseId);
}
