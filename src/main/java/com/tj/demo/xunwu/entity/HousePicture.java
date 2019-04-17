package com.tj.demo.xunwu.entity;

import javax.persistence.*;

@Entity
@Table(name="house_picture")
public class HousePicture {

  @Id
  @GeneratedValue(strategy = GenerationType.IDENTITY)
  private Integer id;
  @Column(name="house_id")
  private Integer houseId;
  @Column(name="cdn_prefix")
  private String cdnPrefix;
  private Integer width;
  private Integer height;
  private String location;
  private String path;


  public Integer getId() {
    return id;
  }

  public void setId(Integer id) {
    this.id = id;
  }


  public Integer getHouseId() {
    return houseId;
  }

  public void setHouseId(Integer houseId) {
    this.houseId = houseId;
  }


  public String getCdnPrefix() {
    return cdnPrefix;
  }

  public void setCdnPrefix(String cdnPrefix) {
    this.cdnPrefix = cdnPrefix;
  }


  public Integer getWidth() {
    return width;
  }

  public void setWidth(Integer width) {
    this.width = width;
  }


  public Integer getHeight() {
    return height;
  }

  public void setHeight(Integer height) {
    this.height = height;
  }


  public String getLocation() {
    return location;
  }

  public void setLocation(String location) {
    this.location = location;
  }


  public String getPath() {
    return path;
  }

  public void setPath(String path) {
    this.path = path;
  }

}
