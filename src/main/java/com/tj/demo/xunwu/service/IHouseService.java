package com.tj.demo.xunwu.service;

import com.tj.demo.xunwu.dto.HouseDTO;
import com.tj.demo.xunwu.dto.HouseSubscribeDTO;
import com.tj.demo.xunwu.form.DatatableSearch;
import com.tj.demo.xunwu.form.HouseForm;
import com.tj.demo.xunwu.form.RentSearch;
import org.springframework.data.util.Pair;

import javax.validation.Valid;

public interface IHouseService {
     ServiceResult<HouseDTO> save(HouseForm houseForm);

     ServiceMultResult<HouseDTO> adminQuery(DatatableSearch searchBody);

    /**
     * 查询完整的房源信息
     * @param id
     * @return
     */
     ServiceResult<HouseDTO> findCompleteOne(Integer id);

    /**
     * 更新房屋信息
     * @param houseForm
     * @return
     */
     ServiceResult<HouseDTO> update(HouseForm houseForm);

    /**
     * 移除照片
     * @param id
     * @return
     */
    ServiceResult removePhoto(Long id);

    /**
     * 更新封面
     * @param coverId
     * @param targetId
     * @return
     */
    ServiceResult updateCover(Long coverId, Long targetId);

    /**
     * 移除标签
     * @param houseId
     * @param tag
     * @return
     */
    ServiceResult removeTag(Long houseId, String tag);

    /**
     * 更新房源状态
     * @param id
     * @param value
     * @return
     */
    ServiceResult updateStatus(Long id, int value);

    /**
     * 房屋发布
     * @param houseId
     * @return
     */
    ServiceResult finishSubscribe(Long houseId);

    ServiceMultResult<Pair<HouseDTO,HouseSubscribeDTO>> findSubscribeList(int start, int size);

    /**
     * 增加标签接口
     * @param houseId
     * @param tag
     * @return
     */
    ServiceResult addTag(Long houseId, String tag);

    /**
     *
     * @param rentSearch
     */
    ServiceMultResult<HouseDTO> query(RentSearch rentSearch);
}
