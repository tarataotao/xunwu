package com.tj.demo.xunwu.repository;

import com.tj.demo.xunwu.entity.HouseSubscribe;
import com.tj.demo.xunwu.entity.Subway;
import org.springframework.data.repository.CrudRepository;

import java.util.List;

public interface SubwayRepository  extends CrudRepository<Subway,Integer>{
    List<Subway> findAllByCityEnName(String cityEnName);
}
