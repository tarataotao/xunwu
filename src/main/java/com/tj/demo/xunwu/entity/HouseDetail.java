package com.tj.demo.xunwu.entity;

import javax.persistence.*;

@Entity
@Table(name="house_detail")
public class HouseDetail {

  @Id
  @GeneratedValue(strategy = GenerationType.IDENTITY)
  private Integer id;
  private String description;
  @Column(name="layout_desc")
  private String layoutDesc;
  private String traffic;
  @Column(name="round_service")
  private String roundService;
  @Column(name="rent_way")
  private Integer rentWay;
  private String address;
  @Column(name="subway_line_id")
  private Integer subwayLineId;
  @Column(name="subway_line_name")
  private String subwayLineName;
  @Column(name="subway_station_id")
  private Integer subwayStationId;
  @Column(name="subway_station_name")
  private String subwayStationName;
  @Column(name="house_id")
  private Integer houseId;


  public Integer getId() {
    return id;
  }

  public void setId(Integer id) {
    this.id = id;
  }


  public String getDescription() {
    return description;
  }

  public void setDescription(String description) {
    this.description = description;
  }


  public String getLayoutDesc() {
    return layoutDesc;
  }

  public void setLayoutDesc(String layoutDesc) {
    this.layoutDesc = layoutDesc;
  }


  public String getTraffic() {
    return traffic;
  }

  public void setTraffic(String traffic) {
    this.traffic = traffic;
  }


  public String getRoundService() {
    return roundService;
  }

  public void setRoundService(String roundService) {
    this.roundService = roundService;
  }


  public Integer getRentWay() {
    return rentWay;
  }

  public void setRentWay(Integer rentWay) {
    this.rentWay = rentWay;
  }


  public String getAddress() {
    return address;
  }

  public void setAddress(String address) {
    this.address = address;
  }


  public Integer getSubwayLineId() {
    return subwayLineId;
  }

  public void setSubwayLineId(Integer subwayLineId) {
    this.subwayLineId = subwayLineId;
  }


  public String getSubwayLineName() {
    return subwayLineName;
  }

  public void setSubwayLineName(String subwayLineName) {
    this.subwayLineName = subwayLineName;
  }


  public Integer getSubwayStationId() {
    return subwayStationId;
  }

  public void setSubwayStationId(Integer subwayStationId) {
    this.subwayStationId = subwayStationId;
  }


  public String getSubwayStationName() {
    return subwayStationName;
  }

  public void setSubwayStationName(String subwayStationName) {
    this.subwayStationName = subwayStationName;
  }


  public Integer getHouseId() {
    return houseId;
  }

  public void setHouseId(Integer houseId) {
    this.houseId = houseId;
  }

}
