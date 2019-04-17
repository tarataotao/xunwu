package com.tj.demo.xunwu.service.impl;

import com.sun.media.sound.ModelMappedInstrument;
import com.tj.demo.xunwu.dto.SubwayDto;
import com.tj.demo.xunwu.dto.SubwayStationDto;
import com.tj.demo.xunwu.dto.SupportAddressDto;
import com.tj.demo.xunwu.entity.Subway;
import com.tj.demo.xunwu.entity.SubwayStation;
import com.tj.demo.xunwu.entity.SupportAddress;
import com.tj.demo.xunwu.repository.SubwayRepository;
import com.tj.demo.xunwu.repository.SubwayStationRepository;
import com.tj.demo.xunwu.repository.SupportAddressRepository;
import com.tj.demo.xunwu.service.ISupportAddress;
import com.tj.demo.xunwu.service.ServiceMultResult;
import com.tj.demo.xunwu.service.ServiceResult;
import org.modelmapper.ModelMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.*;

@Service
public class SupportAddressImpl implements ISupportAddress {

    @Autowired
    private SupportAddressRepository supportAddressRepository;

    @Autowired
    private SubwayRepository subwayRepository;

    @Autowired
    private SubwayStationRepository subwayStationRepository;

    @Autowired
    private ModelMapper modelMapper;


    @Override
    public ServiceMultResult<SupportAddressDto> findAllCities() {
        List<SupportAddress> addresses=supportAddressRepository.findAllByLevel(SupportAddress.Level.CITY.getValue());
        List<SupportAddressDto> addressDtos=new ArrayList<>();
        for(SupportAddress supportAddress:addresses){
        SupportAddressDto target=modelMapper.map(supportAddress,SupportAddressDto.class);
        addressDtos.add(target);
        }
        return new ServiceMultResult<>(addressDtos.size(),addressDtos);
    }

    @Override
    public List<SubwayDto> findAllSubwayByCity(String cityEnName) {
        List<SubwayDto> result=new ArrayList<>();
        List<Subway> subways=subwayRepository.findAllByCityEnName(cityEnName);
        if(subways.isEmpty()){
            return result;
        }
        subways.forEach(subway -> result.add(modelMapper.map(subway,SubwayDto.class)));
        return result;
    }

    @Override
    public List<SubwayStationDto> findAllStationBySubway(Integer subwayId) {
        List<SubwayStationDto> result=new ArrayList<>();
        List<SubwayStation> stations=subwayStationRepository.findAllBySubwayId(subwayId);
        if(stations.isEmpty()){
            return result;
        }
        stations.forEach(subwayStation -> result.add(modelMapper.map(subwayStation,SubwayStationDto.class)));
        return result;
    }

    @Override
    public ServiceMultResult<SupportAddressDto> findAllRegionsByCityName(String cityName) {
        if(cityName==null){
            return new ServiceMultResult<>(0,null);
        }
        List<SupportAddressDto> result=new ArrayList();
        List<SupportAddress> regions=supportAddressRepository.findAllByLevelAndBelongTo(SupportAddress.Level.REGION
                .getValue(), cityName);
        regions.forEach(region->result.add(modelMapper.map(region,SupportAddressDto.class)));
        return new ServiceMultResult<>(regions.size(),result);
    }

    @Override
    public Map<SupportAddress.Level, SupportAddressDto> findCityAndRegion(String cityEnName, String regionEnName) {
        Map<SupportAddress.Level,SupportAddressDto> result=new HashMap<>();
        SupportAddress city = supportAddressRepository.findByEnNameAndLevel(cityEnName, SupportAddress.Level.CITY
                .getValue());
        SupportAddress region=supportAddressRepository.findByEnNameAndBelongTo(regionEnName, city.getEnName());
        result.put(SupportAddress.Level.CITY, modelMapper.map(city, SupportAddressDto.class));
        result.put(SupportAddress.Level.REGION, modelMapper.map(region, SupportAddressDto.class));
        return result;
    }

    @Override
    public ServiceResult<SubwayDto> findSubway(Integer subwayLineId) {
        if(subwayLineId==null){
            return ServiceResult.notFound();
        }
        Optional<Subway> subwayOptional=subwayRepository.findById(subwayLineId);
        Subway subway=subwayOptional.get();
        if(subway==null){
            return ServiceResult.notFound();
        }
        return ServiceResult.of(modelMapper.map(subway,SubwayDto.class));
    }

    @Override
    public ServiceResult<SubwayStationDto> findSubwayStation(Integer subwayStationId) {
        if(subwayStationId!=null){
            return ServiceResult.notFound();
        }
        Optional<SubwayStation> subwayStationOptional=subwayStationRepository.findById(subwayStationId);
        SubwayStation station=subwayStationOptional.get();
        if(station==null){
            return ServiceResult.notFound();
        }
        return ServiceResult.of(modelMapper.map(station,SubwayStationDto.class));
    }

    @Override
    public ServiceResult<SupportAddressDto> findCity(String cityEnName) {
        if(cityEnName==null){
            return ServiceResult.notFound();
        }
        SupportAddress supportAddress=supportAddressRepository.findByEnNameAndLevel(cityEnName,SupportAddress.Level.CITY.getValue());
        if(supportAddress==null){
            return ServiceResult.notFound();
        }
        SupportAddressDto addressDto =modelMapper.map(supportAddress,SupportAddressDto.class);
        return ServiceResult.of(addressDto);
    }
}
