package com.tj.demo.xunwu.repository;

import com.tj.demo.xunwu.entity.House;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.CrudRepository;
import org.springframework.data.repository.PagingAndSortingRepository;
import org.springframework.data.repository.query.Param;

public interface HouseRepository extends PagingAndSortingRepository<House,Integer>,JpaSpecificationExecutor<House> {

    @Modifying
    @Query("update House as house set house.cover=:cover where house.id = :id")
    void updateCover(Long targetId, String path);

    @Modifying
    @Query("update House as house set house.status=:status where house.id=:id")
    void updateStatus(@Param(value = "id") Integer id,@Param(value="status") int status);

    @Modifying
    @Query(" update House as house set house.watchTimes = house.watchTimes + 1 where house.id = :id ")
    void updateWatchTimes(Integer houseId);
}
