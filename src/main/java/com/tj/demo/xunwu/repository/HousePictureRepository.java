package com.tj.demo.xunwu.repository;

import com.tj.demo.xunwu.entity.HousePicture;
import org.springframework.data.repository.CrudRepository;

import java.util.List;

public interface HousePictureRepository extends CrudRepository<HousePicture,Integer> {
    List<HousePicture> findAllByHouseId(Integer id);
}
