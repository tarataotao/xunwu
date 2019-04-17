package com.tj.demo.xunwu.service;

import com.tj.demo.xunwu.dto.SubwayDto;
import com.tj.demo.xunwu.dto.SubwayStationDto;
import com.tj.demo.xunwu.dto.SupportAddressDto;
import com.tj.demo.xunwu.entity.SupportAddress;

import java.util.List;
import java.util.Map;

public interface ISupportAddress {

    /**
     * 获取所有的城市
     * @return
     */
    ServiceMultResult<SupportAddressDto> findAllCities();

    /**
     *
     * @param cityEnName
     * @return
     */
    List<SubwayDto> findAllSubwayByCity(String cityEnName);

    /**
     * 获取地铁线路所有的站点
     * @param subwayId
     * @return
     */
    List<SubwayStationDto> findAllStationBySubway(Integer subwayId);

    /**
     * 根据城市英文简写获取改城市所有支持的区域信息
     * @param cityName
     * @return
     */
    ServiceMultResult<SupportAddressDto> findAllRegionsByCityName(String cityName);

    /**
     * 根据英文简写获取具体区域的信息
     * @param cityEnName
     * @param regionEnName
     * @return
     */
    Map<SupportAddress.Level, SupportAddressDto> findCityAndRegion(String cityEnName, String regionEnName);

    /**
     * 查找地铁线路
     * @param subwayLineId
     * @return
     */
    ServiceResult<SubwayDto> findSubway(Integer subwayLineId);

    ServiceResult<SubwayStationDto> findSubwayStation(Integer subwayStationId);

    ServiceResult<SupportAddressDto> findCity(String cityEnName);
}
