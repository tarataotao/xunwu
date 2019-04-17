package com.tj.demo.xunwu.repository;

import com.tj.demo.xunwu.entity.SubwayStation;
import org.springframework.data.repository.CrudRepository;

import java.util.List;

public interface SubwayStationRepository extends CrudRepository<SubwayStation,Integer> {

    List<SubwayStation> findAllBySubwayId(Integer subwayId);
}
