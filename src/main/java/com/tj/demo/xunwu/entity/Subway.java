package com.tj.demo.xunwu.entity;

import javax.persistence.*;

@Entity
@Table(name = "subway")
public class Subway {

  @Id
  @GeneratedValue(strategy = GenerationType.IDENTITY)
  private Integer id;
  private String name;
  @Column(name="city_en_name")
  private String cityEnName;


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


  public String getCityEnName() {
    return cityEnName;
  }

  public void setCityEnName(String cityEnName) {
    this.cityEnName = cityEnName;
  }

}
