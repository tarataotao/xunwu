package com.tj.demo.xunwu.repository;

import com.tj.demo.xunwu.entity.HouseTag;
import org.springframework.data.repository.CrudRepository;

import java.util.List;

public interface HouseTagRepository extends CrudRepository<HouseTag,Integer> {
    List<HouseTag> findAllByHouseId(Integer id);

    HouseTag findByNameAndHouseId(String tag, Long houseId);

    List<HouseTag> findAllByHouseIdIn(List<Integer> houseIds);
}
