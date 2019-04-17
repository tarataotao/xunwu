package com.tj.demo.xunwu.controller;

import com.google.common.base.Strings;
import com.google.gson.Gson;
import com.qiniu.common.QiniuException;
import com.qiniu.http.Response;
import com.tj.demo.xunwu.base.*;
import com.tj.demo.xunwu.dto.*;
import com.tj.demo.xunwu.entity.SupportAddress;
import com.tj.demo.xunwu.form.DatatableSearch;
import com.tj.demo.xunwu.form.HouseForm;
import com.tj.demo.xunwu.form.RentSearch;
import com.tj.demo.xunwu.service.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.util.Pair;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import javax.annotation.Resource;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpSession;
import javax.validation.Valid;
import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Map;

@Controller
public class AdminController {

    @Autowired
    private IQiNiuService qiNiuService;
    @Autowired
    private Gson gson;
    @Autowired
    private ISupportAddress supportAddress;
    @Autowired
    private IHouseService houseService;
    @Autowired
    private IUserService userService;

    /**
     * 后台管理中心
     * @return
     */
    @GetMapping("/admin/center")
    public String adminCenterPage() {
        return "admin/center";
    }

    /**
     * 欢迎页
     * @return
     */
    @GetMapping("/admin/welcome")
    public String welcomePage() {
        return "admin/welcome";
    }

    /**
     * 管理员登录页
     * @return
     */
    @GetMapping("/admin/login")
    public String adminLoginPage() {
        return "admin/login";
    }


    /**
     * 新增房源功能页
     * @return
     */
    @GetMapping("admin/add/house")
    public String addHousePage() {
        return "admin/house-add";
    }

    @PostMapping(value = "admin/upload/photo",consumes= MediaType.MULTIPART_FORM_DATA_VALUE)
    @ResponseBody
    public ApiResponse uploadPhoto(@RequestParam("file")MultipartFile file){
        if(file.isEmpty()){
            return ApiResponse.ofStatus(ApiResponse.Status.NOT_VALID_PARAM);
        }

        String fileName=file.getOriginalFilename();
        try {
            Response response= qiNiuService.uplodadFile(file.getInputStream());//直接上传至七牛云
            if(response.isOK()){
                System.out.println(response.bodyString());
               QiNiuPutRet ret= gson.fromJson(response.bodyString(), QiNiuPutRet.class);
                return ApiResponse.ofSuccess(ret);
            }else{
                return ApiResponse.ofMessage(response.statusCode,response.getInfo());
            }
        }catch (QiniuException e){
            Response response=e.response;
            try {
                return ApiResponse.ofMessage(response.statusCode,response.bodyString());
            } catch (QiniuException e1) {
                e1.printStackTrace();
                return ApiResponse.ofStatus(ApiResponse.Status.INTERNAL_SERVER_ERROR);
            }
        }
        catch (IOException e) {
            e.printStackTrace();
            return ApiResponse.ofStatus(ApiResponse.Status.INTERNAL_SERVER_ERROR);
        }
    }


    @PostMapping("admin/add/house")
    @ResponseBody
    public ApiResponse addHouse(@ModelAttribute("form-house-add") HouseForm houseForm, BindingResult bindingResult){
        //验证参数
        if(bindingResult.hasErrors()){
            return new ApiResponse(HttpStatus.BAD_REQUEST.value(),bindingResult.getAllErrors().get(0).getDefaultMessage(),null);
        }
        if(houseForm.getPhotos()==null || houseForm.getCover()==null){
            return  ApiResponse.ofMessage(HttpStatus.BAD_REQUEST.value(),"必须上传图片");
        }
        Map<SupportAddress.Level, SupportAddressDto> addressMap= supportAddress.findCityAndRegion(houseForm.getCityEnName(),houseForm.getRegionEnName());
        if(addressMap.keySet().size()!=2){
            return ApiResponse.ofStatus(ApiResponse.Status.NOT_VALID_PARAM);
        }
        ServiceResult<HouseDTO> result=houseService.save(houseForm);
        if(result.isSuccess()){
            return ApiResponse.ofSuccess(result.getResult());
        }
        return ApiResponse.ofStatus(ApiResponse.Status.NOT_VALID_PARAM);
    }

    /**
     * 房源列表
     * @return
     */

    @GetMapping("admin/house/list")
    public String houseListPage(){
        return "admin/house-list";
    }

    @PostMapping("admin/houses")
    @ResponseBody
    public ApiDataTableResponse houses(@ModelAttribute DatatableSearch searchBody){
       ServiceMultResult<HouseDTO> result= houseService.adminQuery(searchBody);
       ApiDataTableResponse response=new ApiDataTableResponse(ApiResponse.Status.SUCCESS);
        response.setData(result.getResult());
        response.setRecordsFiltered(result.getTotal());
        response.setRecordsTotal(result.getTotal());
        response.setDraw(searchBody.getDraw());//必须回写，防伪验证
        return response;
    }

    /**
     * 房源信息编辑也
     * @return
     */
    @GetMapping("admin/house/edit")
    public String houseEditPage(@RequestParam(value = "id")Integer id, Model model){
        if(id==null || id<1){
            return "404";
        }
        ServiceResult<HouseDTO> serviceResult=houseService.findCompleteOne(id);//查询完整的房源信息
        if(!serviceResult.isSuccess()){
            return "404";
        }
        HouseDTO houseDTO=serviceResult.getResult();
        model.addAttribute("house",houseDTO);

        Map<SupportAddress.Level,SupportAddressDto> addressMap=supportAddress.findCityAndRegion(houseDTO.getCityEnName(),houseDTO.getRegionEnName());
        model.addAttribute("city",addressMap.get(SupportAddress.Level.CITY));
        model.addAttribute("region",addressMap.get(SupportAddress.Level.REGION));

        HouseDetailDTO detailDTO=houseDTO.getHouseDetail();
        ServiceResult<SubwayDto> subwayDtoServiceResult=supportAddress.findSubway(detailDTO.getSubwayLineId());
        if(subwayDtoServiceResult.isSuccess()){
            model.addAttribute("subway",subwayDtoServiceResult.getResult());
        }
        ServiceResult<SubwayStationDto> subwayStationDtoServiceResult=supportAddress.findSubwayStation(detailDTO.getSubwayStationId());
        if(subwayStationDtoServiceResult.isSuccess()){
            model.addAttribute("station",subwayStationDtoServiceResult.getResult());
        }
        return "admin/house-edit";
    }

    @PostMapping("admin/house/edit")
    @ResponseBody
    public ApiResponse saveHouse(@Valid @ModelAttribute("form-house-edit") HouseForm houseForm,BindingResult bindingResult){
        if(bindingResult.hasErrors()){
            return new ApiResponse(HttpStatus.BAD_REQUEST.value(),bindingResult.getAllErrors().get(0).getDefaultMessage(),null);
        }
        Map<SupportAddress.Level,SupportAddressDto> addressDtoMap=supportAddress.findCityAndRegion(houseForm.getCityEnName(),houseForm.getRegionEnName());
        if(addressDtoMap.keySet().size()!=2){
            return ApiResponse.ofSuccess(ApiResponse.Status.NOT_VALID_PARAM);
        }
        ServiceResult result=houseService.update(houseForm);
        if(result.isSuccess()){
            return ApiResponse.ofSuccess(null);
        }

        ApiResponse response=ApiResponse.ofStatus(ApiResponse.Status.BAD_REQUEST);
        response.setMessage(result.getMessage());
        return response;
    }

    /**
     * 移除图片接口
     * @param id
     * @return
     */
    @DeleteMapping("admin/house/photo")
    @ResponseBody
    public ApiResponse removeHousePhoto(@RequestParam(value = "id") Long id) {
        ServiceResult result = this.houseService.removePhoto(id);

        if (result.isSuccess()) {
            return ApiResponse.ofStatus(ApiResponse.Status.SUCCESS);
        } else {
            return ApiResponse.ofMessage(HttpStatus.BAD_REQUEST.value(), result.getMessage());
        }
    }

    /**
     * 修改封面接口
     * @param coverId
     * @param targetId
     * @return
     */
    @PostMapping("admin/house/cover")
    @ResponseBody
    public ApiResponse updateCover(@RequestParam(value = "cover_id") Long coverId,
                                   @RequestParam(value = "target_id") Long targetId) {
        ServiceResult result = this.houseService.updateCover(coverId, targetId);

        if (result.isSuccess()) {
            return ApiResponse.ofStatus(ApiResponse.Status.SUCCESS);
        } else {
            return ApiResponse.ofMessage(HttpStatus.BAD_REQUEST.value(), result.getMessage());
        }
    }

    /**
     * 增加标签接口
     * @param houseId
     * @param tag
     * @return
     */
    @PostMapping("admin/house/tag")
    @ResponseBody
    public ApiResponse addHouseTag(@RequestParam(value = "house_id") Long houseId,
                                   @RequestParam(value = "tag") String tag) {
        if (houseId < 1 || Strings.isNullOrEmpty(tag)) {
            return ApiResponse.ofStatus(ApiResponse.Status.BAD_REQUEST);
        }

        ServiceResult result = this.houseService.addTag(houseId, tag);
        if (result.isSuccess()) {
            return ApiResponse.ofStatus(ApiResponse.Status.SUCCESS);
        } else {
            return ApiResponse.ofMessage(HttpStatus.BAD_REQUEST.value(), result.getMessage());
        }
    }

    /**
     * 移除标签接口
     * @param houseId
     * @param tag
     * @return
     */
    @DeleteMapping("admin/house/tag")
    @ResponseBody
    public ApiResponse removeHouseTag(@RequestParam(value = "house_id") Long houseId,
                                      @RequestParam(value = "tag") String tag) {
        if (houseId < 1 || Strings.isNullOrEmpty(tag)) {
            return ApiResponse.ofStatus(ApiResponse.Status.BAD_REQUEST);
        }

        ServiceResult result = this.houseService.removeTag(houseId, tag);
        if (result.isSuccess()) {
            return ApiResponse.ofStatus(ApiResponse.Status.SUCCESS);
        } else {
            return ApiResponse.ofMessage(HttpStatus.BAD_REQUEST.value(), result.getMessage());
        }
    }

    /**
     * 审核接口
     * @param id
     * @param operation
     * @return
     */
    @PutMapping("admin/house/operate/{id}/{operation}")
    @ResponseBody
    public ApiResponse operateHouse(@PathVariable(value = "id") Long id,
                                    @PathVariable(value = "operation") int operation) {
        if (id <= 0) {
            return ApiResponse.ofStatus(ApiResponse.Status.NOT_VALID_PARAM);
        }
        ServiceResult result;

        switch (operation) {
            case HouseOperation.PASS:
                result = this.houseService.updateStatus(id, HouseStatus.PASSES.getValue());
                break;
            case HouseOperation.PULL_OUT:
                result = this.houseService.updateStatus(id, HouseStatus.NOT_AUDITED.getValue());
                break;
            case HouseOperation.DELETE:
                result = this.houseService.updateStatus(id, HouseStatus.DELETED.getValue());
                break;
            case HouseOperation.RENT:
                result = this.houseService.updateStatus(id, HouseStatus.RENTED.getValue());
                break;
            default:
                return ApiResponse.ofStatus(ApiResponse.Status.BAD_REQUEST);
        }

        if (result.isSuccess()) {
            return ApiResponse.ofSuccess(null);
        }
        return ApiResponse.ofMessage(HttpStatus.BAD_REQUEST.value(),
                result.getMessage());
    }

    @GetMapping("admin/house/subscribe")
    public String houseSubscribe() {
        return "admin/subscribe";
    }

    @GetMapping("admin/house/subscribe/list")
    @ResponseBody
    public ApiResponse subscribeList(@RequestParam(value = "draw") int draw,
                                     @RequestParam(value = "start") int start,
                                     @RequestParam(value = "length") int size) {
        ServiceMultResult<Pair<HouseDTO, HouseSubscribeDTO>> result = houseService.findSubscribeList(start, size);

        ApiDataTableResponse response = new ApiDataTableResponse(ApiResponse.Status.SUCCESS);
        response.setData(result.getResult());
        response.setDraw(draw);
        response.setRecordsFiltered(result.getTotal());
        response.setRecordsTotal(result.getTotal());
        return response;
    }

    @GetMapping("admin/user/{userId}")
    @ResponseBody
    public ApiResponse getUserInfo(@PathVariable(value = "userId") Long userId) {
        if (userId == null || userId < 1) {
            return ApiResponse.ofStatus(ApiResponse.Status.BAD_REQUEST);
        }

        ServiceResult<UserDTO> serviceResult = userService.findById(userId);
        if (!serviceResult.isSuccess()) {
            return ApiResponse.ofStatus(ApiResponse.Status.NOT_FOUND);
        } else {
            return ApiResponse.ofSuccess(serviceResult.getResult());
        }
    }

    @PostMapping("admin/finish/subscribe")
    @ResponseBody
    public ApiResponse finishSubscribe(@RequestParam(value = "house_id") Long houseId) {
        if (houseId < 1) {
            return ApiResponse.ofStatus(ApiResponse.Status.BAD_REQUEST);
        }

        ServiceResult serviceResult = houseService.finishSubscribe(houseId);
        if (serviceResult.isSuccess()) {
            return ApiResponse.ofSuccess("");
        } else {
            return ApiResponse.ofMessage(ApiResponse.Status.BAD_REQUEST.getCode(), serviceResult.getMessage());
        }
    }

}
