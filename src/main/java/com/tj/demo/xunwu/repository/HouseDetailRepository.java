package com.tj.demo.xunwu.repository;

import com.tj.demo.xunwu.entity.HouseDetail;
import org.springframework.data.repository.CrudRepository;

import java.util.List;

public interface HouseDetailRepository extends CrudRepository<HouseDetail,Integer> {
    HouseDetail findByHouseId(Integer id);

    List<HouseDetail> findAllByHouseIdIn(List<Integer> houseIds);
}
