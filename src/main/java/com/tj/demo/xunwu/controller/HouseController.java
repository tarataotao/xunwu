package com.tj.demo.xunwu.controller;

import com.tj.demo.xunwu.base.ApiResponse;
import com.tj.demo.xunwu.base.RentValueBlock;
import com.tj.demo.xunwu.dto.*;
import com.tj.demo.xunwu.entity.House;
import com.tj.demo.xunwu.entity.SupportAddress;
import com.tj.demo.xunwu.form.RentSearch;
import com.tj.demo.xunwu.service.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import javax.servlet.http.HttpSession;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

@Controller
public class HouseController {

    @Autowired
    private ISupportAddress supportAddress;
    @Autowired
    private IHouseService houseService;

    @Autowired
    private IUserService userService;

    @Autowired
    private ISearchService searchService;


    @GetMapping("rent/house/autocomplete")
    @ResponseBody
    public ApiResponse autoComplete(@RequestParam(value = "prefix") String prefix){
        if(prefix.isEmpty()){
            return ApiResponse.ofSuccess(ApiResponse.Status.BAD_REQUEST);
        }
       ServiceResult<List<String>> result=this.searchService.suggest(prefix);
        return ApiResponse.ofSuccess(result.getResult());
    }


    /**
     * 获取支持城市列表
     * @return
     */
    @GetMapping("address/support/cities")
    @ResponseBody
    public ApiResponse getSupportCities(){
        ServiceMultResult<SupportAddressDto> result=supportAddress.findAllCities();
        if(result.getResultSize()==0){
            return ApiResponse.ofStatus(ApiResponse.Status.NOT_FOUND);
        }
        return ApiResponse.ofSuccess(result);
    }

    /**
     * 获取对应城市支持区域列表
     * @param cityEnName
     * @return
     */
    @GetMapping("address/support/regions")
    @ResponseBody
    public ApiResponse getSupportRegions(@RequestParam(name="city_name")String cityEnName){
        ServiceMultResult<SupportAddressDto> result=supportAddress.findAllRegionsByCityName(cityEnName);
        if(result.getResult()==null || result.getTotal()<1) {
            return ApiResponse.ofStatus(ApiResponse.Status.NOT_FOUND);
        }
        return ApiResponse.ofSuccess(result.getResult());
    }

    /**
     * 获取集体城市所支持的地铁线路
     * @param cityEnName
     * @return
     */
    @GetMapping("address/spport/subway/line")
    @ResponseBody
    public ApiResponse getSupportSubwayLine(@RequestParam(name="city_name") String cityEnName){
        List<SubwayDto> subways=supportAddress.findAllSubwayByCity(cityEnName);
        if(subways.isEmpty()){
           return ApiResponse.ofStatus(ApiResponse.Status.NOT_FOUND);
        }
        return ApiResponse.ofSuccess(subways);
    }

    /**
     * 获取对应地铁线路所支持的地铁站点
     * @param subwayId
     * @return
     */
    @GetMapping("address/support/subway/station")
    @ResponseBody
    public ApiResponse getSupportSubwayStation(@RequestParam(name="subway_id")Integer subwayId){
        List<SubwayStationDto> stationDtos=supportAddress.findAllStationBySubway(subwayId);
        if(stationDtos.isEmpty()){
            return ApiResponse.ofStatus(ApiResponse.Status.NOT_FOUND);
        }
        return ApiResponse.ofSuccess(stationDtos);
    }

    @GetMapping("rent/house")
    public String rentHousePage(@ModelAttribute RentSearch rentSearch, Model model,
                                HttpSession session, RedirectAttributes redirectAttributes){
        if(rentSearch.getCityEnName()==null){
            String cityEnNameInSession= (String) session.getAttribute("cityEnName");
            if(cityEnNameInSession==null){
                redirectAttributes.addAttribute("msg","must_chose_city");
                return "redirect:/index";
            }else{
                rentSearch.setCityEnName(cityEnNameInSession);
            }
        }else{
            session.setAttribute("cityEnName",rentSearch.getCityEnName());
        }
        ServiceResult<SupportAddressDto> city=supportAddress.findCity(rentSearch.getCityEnName());
        if(!city.isSuccess()){
            redirectAttributes.addAttribute("msg","must_chose_city");
            return "redirect:/index";
        }
        model.addAttribute("currentCity",city);
        ServiceMultResult<SupportAddressDto> addressResult=supportAddress.findAllRegionsByCityName(rentSearch.getCityEnName());
        if(addressResult.getResult()==null || addressResult.getTotal()<1){
            redirectAttributes.addAttribute("msg","must_chose_city");
            return "redirect:/index";
        }
        ServiceMultResult<HouseDTO> houseDto=  houseService.query(rentSearch);
        model.addAttribute("total",houseDto.getTotal());
        model.addAttribute("houses",houseDto.getResult());
        if(rentSearch.getRegionEnName()==null){
            rentSearch.setRegionEnName("*");
        }
        model.addAttribute("searchBody",rentSearch);
        model.addAttribute("regions",addressResult.getResult());

        model.addAttribute("priceBlocks", RentValueBlock.PRICE_BLOCK);
        model.addAttribute("areaBlocks",RentValueBlock.AREA_BLOCK);

        model.addAttribute("currentPriceBlock",RentValueBlock.matchPrice(rentSearch.getPriceBlock()));
        model.addAttribute("currentAreaBlock",RentValueBlock.matchArea(rentSearch.getAreaBlock()));
        return "rent-list";

    }

    @GetMapping("rent/house/show/{id}")
    public String show(@PathVariable(value = "id") Integer houseId,Model model){
        if(houseId<=0){
            return "404";
        }
        ServiceResult<HouseDTO> serviceResult=houseService.findCompleteOne(houseId);
        if(!serviceResult.isSuccess()){
            return "404";
        }
        HouseDTO houseDTO=serviceResult.getResult();
        Map<SupportAddress.Level, SupportAddressDto> addressDtoMap=supportAddress.findCityAndRegion(houseDTO.getCityEnName(),houseDTO.getRegionEnName());
        SupportAddressDto city=addressDtoMap.get(SupportAddress.Level.CITY);
        SupportAddressDto region=addressDtoMap.get(SupportAddress.Level.REGION);
        ServiceResult<UserDTO> userDTOServiceResult=userService.findById(houseDTO.getAdminId());
        model.addAttribute("house", houseDTO);
        model.addAttribute("agent",userDTOServiceResult.getResult());
        model.addAttribute("city",city);
        model.addAttribute("region",region);
        model.addAttribute("houseCountInDistrict",0);
        return "house-detail";
    }
}
