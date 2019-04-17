package com.tj.demo.xunwu.entity;

import javax.persistence.*;

@Entity
@Table(name="house_tag")
public class HouseTag {

  @Column(name="house_id")
  private Integer houseId;
  @Id
  @GeneratedValue(strategy = GenerationType.IDENTITY)
  private Integer id;
  private String name;

  public HouseTag() {
  }

  public HouseTag(Integer houseId, String name) {
    this.houseId = houseId;
    this.name = name;
  }

  public Integer getHouseId() {
    return houseId;
  }

  public void setHouseId(Integer houseId) {
    this.houseId = houseId;
  }


  public Integer getId() {
    return id;
  }

  public void setId(Integer id) {
    this.id = id;
  }


  public String getName() {
    return name;
  }

  public void setName(String name) {
    this.name = name;
  }

}
