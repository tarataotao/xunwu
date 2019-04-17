package com.tj.demo.xunwu.service.impl;

import com.google.common.collect.Maps;
import com.qiniu.common.QiniuException;
import com.qiniu.http.Response;
import com.tj.demo.xunwu.base.HouseSort;
import com.tj.demo.xunwu.base.HouseStatus;
import com.tj.demo.xunwu.base.HouseSubscribeStatus;
import com.tj.demo.xunwu.base.LoginUserUtil;
import com.tj.demo.xunwu.dto.HouseDTO;
import com.tj.demo.xunwu.dto.HouseDetailDTO;
import com.tj.demo.xunwu.dto.HousePictureDTO;
import com.tj.demo.xunwu.dto.HouseSubscribeDTO;
import com.tj.demo.xunwu.entity.*;
import com.tj.demo.xunwu.form.DatatableSearch;
import com.tj.demo.xunwu.form.HouseForm;
import com.tj.demo.xunwu.form.PhotoForm;
import com.tj.demo.xunwu.form.RentSearch;
import com.tj.demo.xunwu.repository.*;
import com.tj.demo.xunwu.service.IHouseService;
import com.tj.demo.xunwu.service.IQiNiuService;
import com.tj.demo.xunwu.service.ServiceMultResult;
import com.tj.demo.xunwu.service.ServiceResult;
import org.modelmapper.ModelMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.data.util.Pair;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import javax.persistence.criteria.Predicate;
import java.util.*;

@Service
public class HouseServiceImpl implements IHouseService {

    @Autowired
    private ModelMapper modelMapper;
    @Autowired
    private HouseRepository houseRepository;
    @Autowired
    private SubwayRepository subwayRepository;
    @Autowired
    private SubwayStationRepository subwayStationRepository;
    @Autowired
    private HouseDetailRepository houseDetailRepository;
    @Autowired
    private HousePictureRepository housePictureRepository;
    @Autowired
    private HouseTagRepository houseTagRepository;
    @Value("${qiniu.cdn.prefix}")
    private String cdnPrefix;
    @Autowired
    private IQiNiuService qiNiuService;
    @Autowired
    private HouseSubscribeRespository subscribeRespository;



    @Override
    @Transactional
    public ServiceResult<HouseDTO> save(HouseForm houseForm) {
        HouseDetail detail=new HouseDetail();
        ServiceResult<HouseDTO> subwayValidationResult=wrapperDetailInfo(detail,houseForm);
        if (subwayValidationResult != null) {
            return subwayValidationResult;
        }
        House house=new House();
        modelMapper.map(houseForm,house);
        Date now=new Date();
        house.setCreateTime(now);
        house.setLastUpdateTime(now);
        house.setAdminId(LoginUserUtil.getLoginUserId());//TODO
        houseRepository.save(house);
        detail.setHouseId(house.getId());
        detail=houseDetailRepository.save(detail);
        List<HousePicture> pictures=generatePictures(houseForm,house.getId());
        Iterable<HousePicture> housePictures=housePictureRepository.saveAll(pictures);
        HouseDTO houseDTO=modelMapper.map(house,HouseDTO.class);
        HouseDetailDTO houseDetailDTO=modelMapper.map(detail,HouseDetailDTO.class);
        houseDTO.setHouseDetail(houseDetailDTO);
        List<HousePictureDTO> pictureDTOS=new ArrayList<>();
        housePictures.forEach(housePicture -> pictureDTOS.add(modelMapper.map(housePicture,HousePictureDTO.class)));
        houseDTO.setPictures(pictureDTOS);
        houseDTO.setCover(this.cdnPrefix+houseDTO.getCover());
        List<String> tags=houseForm.getTags();
        if(tags != null || tags.isEmpty()){
            List<HouseTag> houseTags=new ArrayList();
            for(String tag:tags){
                houseTags.add(new HouseTag(house.getId(),tag));
            }
            houseTagRepository.saveAll(houseTags);
            houseDTO.setTags(tags);
        }
        return new ServiceResult<HouseDTO>(true,null,houseDTO);
    }

    @Override
    public ServiceMultResult<HouseDTO> adminQuery(DatatableSearch searchBody) {
        List<HouseDTO> houseDTOS=new ArrayList<>();
        Sort sort=new Sort(Sort.Direction.fromString(searchBody.getDirection()),searchBody.getOrderBy());
        int page=searchBody.getStart()/searchBody.getLength();
        Pageable pageable=new PageRequest(page,searchBody.getLength(),sort);
        Specification<House> houseSpecification=(root,query,cb)->{
            Predicate predicate=cb.equal(root.get("adminId"),LoginUserUtil.getLoginUserId()); //当前用户的房源信息
predicate=cb.and(predicate,cb.notEqual(root.get("status"), HouseStatus.DELETED.getValue())); //未删除的数据
            if(searchBody.getCity() !=null){
                predicate=cb.and(predicate,cb.equal(root.get("cityEnName"),searchBody.getCity()));
            }
            if(searchBody.getStatus()!=null){
                predicate=cb.and(predicate,cb.equal(root.get("status"),searchBody.getStatus()));
            }
            if(searchBody.getCreateTimeMin() !=null ){
                predicate=cb.and(predicate,cb.greaterThanOrEqualTo(root.get("createTime"),searchBody.getCreateTimeMin()));
            }
            if(searchBody.getCreateTimeMax()!=null){
                predicate=cb.and(predicate,cb.greaterThanOrEqualTo(root.get("createTime"),searchBody.getCreateTimeMax()));
            }
            if(searchBody.getTitle() !=null ){
                predicate=cb.and(predicate,cb.like(root.get("title"),"%"+searchBody.getTitle()+"%"));
            }
            return predicate;
        };
//        Page<House> houses=houseRepository.findAll(pageable);
        Page<House> houses=houseRepository.findAll(houseSpecification,pageable);
        houses.forEach(house -> {
            HouseDTO houseDTO=modelMapper.map(house,HouseDTO.class);
            houseDTO.setCover(this.cdnPrefix+house.getCover());
            houseDTOS.add(houseDTO);
        } );
        return new ServiceMultResult<>(houses.getTotalElements(),houseDTOS);
    }

    /**
     * 查询完整的房源信息
     * @param id
     * @return
     */
    @Override
    public ServiceResult<HouseDTO> findCompleteOne(Integer id) {
        Optional<House> houseOptional=houseRepository.findById(id);
        House house=houseOptional.get();
        if(house==null){
            return ServiceResult.notFound();
        }
        HouseDetail detail=houseDetailRepository.findByHouseId(id);
        List<HousePicture> pictures=housePictureRepository.findAllByHouseId(id);
        HouseDetailDTO detailDTO=modelMapper.map(detail,HouseDetailDTO.class);
        List<HousePictureDTO> pictureDTOS=new ArrayList<>();
        pictures.forEach(picture->pictureDTOS.add(modelMapper.map(picture,HousePictureDTO.class)));
        List<HouseTag> tags=houseTagRepository.findAllByHouseId(id);
        List<String> tagList=new ArrayList<>();
        tags.forEach(tag->tagList.add(tag.getName()));
        HouseDTO result=modelMapper.map(house,HouseDTO.class);
        result.setHouseDetail(detailDTO);
        result.setPictures(pictureDTOS);
        result.setTags(tagList);
        return ServiceResult.of(result);
    }

    @Override
    @Transactional
    public ServiceResult<HouseDTO> update(HouseForm houseForm) {
        Optional<House> houseOptional=this.houseRepository.findById(houseForm.getId());
        House house=houseOptional.get();
        if(house==null){
            return ServiceResult.notFound();
        }
        HouseDetail detail=houseDetailRepository.findByHouseId(house.getId());
        if(detail==null){
            return ServiceResult.notFound();
        }
        ServiceResult wrapperResult=wrapperDetailInfo(detail,houseForm);
        if(wrapperResult!=null){
            return wrapperResult;
        }
        houseDetailRepository.save(detail);
        List<HousePicture> pictures=generatePictures(houseForm,houseForm.getId());
        housePictureRepository.saveAll(pictures);
        if(houseForm.getCover()==null){
            houseForm.setCover(house.getCover());
        }
        modelMapper.map(houseForm,house);
        house.setLastUpdateTime(new Date());
        houseRepository.save(house);
        return ServiceResult.success();
    }

    @Override
    public ServiceResult removePhoto(Long id) {
        Optional<HousePicture> picture=housePictureRepository.findById(Integer.valueOf(id+""));
        if(picture==null){
            return ServiceResult.notFound();
        }
        try {
            //从七牛云中移除掉图片
            Response response=this.qiNiuService.delete(picture.get().getPath());
            if(response.isOK()){
                housePictureRepository.deleteById(Integer.valueOf(id+""));
                return ServiceResult.success();
            }else{
                return new ServiceResult(false,response.error);
            }
        } catch (QiniuException e) {
            e.printStackTrace();
            return new ServiceResult(false,e.getMessage());
        }
    }

    @Override
    @Transactional
    public ServiceResult updateCover(Long coverId, Long targetId) {
        Optional<HousePicture> coverOptional=housePictureRepository.findById(Integer.valueOf(coverId+""));
        HousePicture cover=coverOptional.get();
        if(coverId==null){
            return ServiceResult.notFound();
        }
        houseRepository.updateCover(targetId,cover.getPath());
        return ServiceResult.success();
    }

    @Override
    @Transactional
    public ServiceResult removeTag(Long houseId, String tag) {
        Optional<House> houseOptional=houseRepository.findById(Integer.valueOf(houseId+""));
        House house=houseOptional.get();
        if(house==null){
            return ServiceResult.notFound();
        }
        HouseTag houseTag=houseTagRepository.findByNameAndHouseId(tag,houseId);
        if(houseTag==null){
            return new ServiceResult(false,"标签不存在");
        }
        houseTagRepository.deleteById(houseTag.getId());
        return ServiceResult.success();
    }

    @Override
    @Transactional
    public ServiceResult updateStatus(Long id, int status) {
        Optional<House> houseOptional=houseRepository.findById(Integer.valueOf(id+""));
        House house=houseOptional.get();
        if(house ==null){
            return ServiceResult.notFound();
        }
        if(house.getStatus()==status){
            return new ServiceResult(false,"已出租的房源不允许修改状态");
        }
        if(house.getStatus()==HouseStatus.RENTED.getValue()){
            return new ServiceResult(false,"状态没有发生变化");
        }
        if(house.getStatus() ==HouseStatus.DELETED.getValue()){
            return new ServiceResult(false,"已删除的资源不允许操作");
        }
        houseRepository.updateStatus(id,status);
        return ServiceResult.success();
    }

    @Override
    @Transactional
    public ServiceResult finishSubscribe(Long houseId) {
        Integer adminId=LoginUserUtil.getLoginUserId();
        HouseSubscribe subscribe=subscribeRespository.findByHouseIdAndAdminId(houseId,Long.valueOf(adminId));
        if(subscribe==null){
            return new ServiceResult(false,"无预约记录");
        }
        subscribeRespository.updateStatus(Long.valueOf(subscribe.getId()+""), HouseSubscribeStatus.FINISH.getValue());
        houseRepository.updateWatchTimes(Integer.valueOf(houseId+""));
        return ServiceResult.success();
    }

    @Override
    public ServiceMultResult<Pair<HouseDTO, HouseSubscribeDTO>> findSubscribeList(int start, int size) {
        return null;
    }

    @Override
    @Transactional
    public ServiceResult addTag(Long houseId, String tag) {
        Optional<House> houseOptional=houseRepository.findById(Integer.valueOf(houseId+""));
        House house=houseOptional.get();
        if(house==null){
            return ServiceResult.notFound();
        }
        HouseTag houseTag=houseTagRepository.findByNameAndHouseId(tag,houseId);
        if(houseTag!=null){
            return new ServiceResult(false,"标签已经存在");
        }

        houseTagRepository.save(new HouseTag(Integer.valueOf(houseId+""),tag));
        return ServiceResult.success();
    }

    @Override
    public ServiceMultResult<HouseDTO> query(RentSearch rentSearch) {
        Sort sort= HouseSort.generateSort(rentSearch.getOrderBy(),rentSearch.getOrderDirection());
        int page=rentSearch.getStart()/rentSearch.getSize();
        Pageable pageable=new PageRequest(page,rentSearch.getSize(),sort);
        Specification<House> specification=(root,criteriaQuery,criteriaBuilder)->{
            Predicate predicate=criteriaBuilder.equal(root.get("status"),HouseStatus.PASSES.getValue());
            predicate=criteriaBuilder.and(predicate,criteriaBuilder.equal(root.get("cityEnName"),rentSearch.getCityEnName()));
            if(HouseSort.DISTANCE_TO_SUBWAY_KEY.equals(rentSearch.getOrderBy())){
                predicate=criteriaBuilder.and(predicate,criteriaBuilder.gt(root.get(HouseSort.DISTANCE_TO_SUBWAY_KEY),-1));
            }
            return predicate;
        };
        Page<House> houses=houseRepository.findAll(specification,pageable);
        List<HouseDTO> houseDTOS=new ArrayList<>();

        List<Integer> houseIds=new ArrayList();
        Map<Integer,HouseDTO> idToHouseMap=Maps.newHashMap();

        houses.forEach(house -> {
            HouseDTO houseDTO=modelMapper.map(house,HouseDTO.class);
            houseDTO.setCover(this.cdnPrefix+house.getCover());
            houseDTOS.add(houseDTO);

            houseIds.add(house.getId());
            idToHouseMap.put(house.getId(),houseDTO);
        });

        wrapperHouseList(houseIds,idToHouseMap);
        return new ServiceMultResult<>(houses.getTotalElements(),houseDTOS);
    }

    /**
     * 渲染详细信息及标签
     * @param houseIds
     * @param idToHouseMap
     */
    private void wrapperHouseList(List<Integer > houseIds,Map<Integer,HouseDTO> idToHouseMap){
        List<HouseDetail> details=houseDetailRepository.findAllByHouseIdIn(houseIds);
        details.forEach(houseDetail -> {
            HouseDTO houseDTO=idToHouseMap.get(houseDetail.getHouseId());
            HouseDetailDTO detailDTO=modelMapper.map(houseDetail,HouseDetailDTO.class);
            houseDTO.setHouseDetail(detailDTO);
        });
        List<HouseTag> houseTags=houseTagRepository.findAllByHouseIdIn(houseIds);
        houseTags.forEach(houseTag -> {
            HouseDTO houseDTO=idToHouseMap.get(houseTag.getHouseId());
            houseDTO.getTags().add(houseTag.getName());
        });
    }

    private List<HousePicture> generatePictures(HouseForm form, Integer houseId){
        List<HousePicture> pictures=new ArrayList<>();
        if(form.getPhotos()==null || form.getPhotos().isEmpty()){
            return pictures;
        }
        for(PhotoForm photoForm:form.getPhotos()){
            HousePicture picture=new HousePicture();
            picture.setHouseId(houseId);
            picture.setCdnPrefix(cdnPrefix);
            picture.setPath(photoForm.getPath());
            picture.setWidth(photoForm.getWidth());
            picture.setHeight(photoForm.getHeight());
            pictures.add(picture);
        }
        return pictures;
    }

    private ServiceResult<HouseDTO> wrapperDetailInfo(HouseDetail houseDetail,HouseForm houseForm){
       Optional<Subway> subwayOptional=subwayRepository.findById(houseForm.getSubwayLineId());
        Subway subway=subwayOptional.get();
        if(subway == null){
            return new ServiceResult<>(false,"Not valid subway line!");
        }
        Optional<SubwayStation> subwayStationOptional=subwayStationRepository.findById(houseForm.getSubwayStationId());
        SubwayStation subwayStation=subwayStationOptional.get();
        if(subwayStation == null || subway.getId()!=subwayStation.getSubwayId()){
            return new ServiceResult<>(false,"Not valid subway station!");
        }
        houseDetail.setSubwayLineId(subway.getId());
        houseDetail.setSubwayLineName(subway.getName());
        houseDetail.setSubwayStationId(subwayStation.getId());
        houseDetail.setSubwayStationName(subwayStation.getName());

        houseDetail.setDescription(houseForm.getDescription());
        houseDetail.setAddress(houseForm.getDetailAddress());
        houseDetail.setLayoutDesc(houseForm.getLayoutDesc());
        houseDetail.setRentWay(houseForm.getRentWay());
        houseDetail.setRoundService(houseForm.getRoundService());
        houseDetail.setTraffic(houseForm.getTraffic());
        return null;
    }
}
