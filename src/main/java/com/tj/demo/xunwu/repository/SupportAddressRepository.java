package com.tj.demo.xunwu.repository;

import com.tj.demo.xunwu.entity.SupportAddress;
import org.springframework.data.repository.CrudRepository;

import java.util.List;

public interface SupportAddressRepository extends CrudRepository<SupportAddress,Integer> {

    /**
     * 获取所有对应行政级别的信息
     * @param level
     * @return
     */
    List<SupportAddress> findAllByLevel(String level);

    List<SupportAddress> findAllByLevelAndBelongTo(String value, String cityName);

    SupportAddress findByEnNameAndLevel(String cityEnName, String value);

    SupportAddress findByEnNameAndBelongTo(String regionEnName, String enName);
}
