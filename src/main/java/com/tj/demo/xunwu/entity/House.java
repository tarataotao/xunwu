package com.tj.demo.xunwu.entity;

import javax.persistence.*;

@Entity
@Table(name="house")
public class House {

  @Id
  @GeneratedValue(strategy = GenerationType.IDENTITY)
  private Integer id;
  private String title;
  private Integer price;
  private Integer area;
  private Integer room;
  private Integer floor;
  @Column(name="total_floor")
  private Integer totalFloor;
  @Column(name="watch_times")
  private Integer watchTimes;
  @Column(name="build_year")
  private Integer buildYear;
  private Integer status;
  @Column(name = "create_time")
  private java.util.Date createTime;
  @Column(name="last_update_time")
  private java.util.Date lastUpdateTime;
  @Column(name="city_en_name")
  private String cityEnName;
  @Column(name="region_en_name")
  private String regionEnName;
  private String cover;
  private Integer direction;
  @Column(name="distance_to_subway")
  private Integer distanceToSubway;
  private Integer parlour;
  private String district;
  private Integer adminId;
  private Integer bathroom;
  private String street;


  public Integer getId() {
    return id;
  }

  public void setId(Integer id) {
    this.id = id;
  }


  public String getTitle() {
    return title;
  }

  public void setTitle(String title) {
    this.title = title;
  }


  public Integer getPrice() {
    return price;
  }

  public void setPrice(Integer price) {
    this.price = price;
  }


  public Integer getArea() {
    return area;
  }

  public void setArea(Integer area) {
    this.area = area;
  }


  public Integer getRoom() {
    return room;
  }

  public void setRoom(Integer room) {
    this.room = room;
  }


  public Integer getFloor() {
    return floor;
  }

  public void setFloor(Integer floor) {
    this.floor = floor;
  }


  public Integer getTotalFloor() {
    return totalFloor;
  }

  public void setTotalFloor(Integer totalFloor) {
    this.totalFloor = totalFloor;
  }


  public Integer getWatchTimes() {
    return watchTimes;
  }

  public void setWatchTimes(Integer watchTimes) {
    this.watchTimes = watchTimes;
  }


  public Integer getBuildYear() {
    return buildYear;
  }

  public void setBuildYear(Integer buildYear) {
    this.buildYear = buildYear;
  }


  public Integer getStatus() {
    return status;
  }

  public void setStatus(Integer status) {
    this.status = status;
  }


  public java.util.Date getCreateTime() {
    return createTime;
  }

  public void setCreateTime(java.util.Date createTime) {
    this.createTime = createTime;
  }


  public java.util.Date getLastUpdateTime() {
    return lastUpdateTime;
  }

  public void setLastUpdateTime(java.util.Date lastUpdateTime) {
    this.lastUpdateTime = lastUpdateTime;
  }


  public String getCityEnName() {
    return cityEnName;
  }

  public void setCityEnName(String cityEnName) {
    this.cityEnName = cityEnName;
  }


  public String getRegionEnName() {
    return regionEnName;
  }

  public void setRegionEnName(String regionEnName) {
    this.regionEnName = regionEnName;
  }


  public String getCover() {
    return cover;
  }

  public void setCover(String cover) {
    this.cover = cover;
  }


  public Integer getDirection() {
    return direction;
  }

  public void setDirection(Integer direction) {
    this.direction = direction;
  }


  public Integer getDistanceToSubway() {
    return distanceToSubway;
  }

  public void setDistanceToSubway(Integer distanceToSubway) {
    this.distanceToSubway = distanceToSubway;
  }


  public Integer getParlour() {
    return parlour;
  }

  public void setParlour(Integer parlour) {
    this.parlour = parlour;
  }


  public String getDistrict() {
    return district;
  }

  public void setDistrict(String district) {
    this.district = district;
  }


  public Integer getAdminId() {
    return adminId;
  }

  public void setAdminId(Integer adminId) {
    this.adminId = adminId;
  }


  public Integer getBathroom() {
    return bathroom;
  }

  public void setBathroom(Integer bathroom) {
    this.bathroom = bathroom;
  }


  public String getStreet() {
    return street;
  }

  public void setStreet(String street) {
    this.street = street;
  }

}
