package com.tj.demo.xunwu.entity;


import javax.persistence.*;

@Entity
@Table(name="subway_station")
public class SubwayStation {

  @Id
  @GeneratedValue(strategy = GenerationType.IDENTITY)
  private Integer id;
  @Column(name="subway_id")
  private Integer subwayId;
  private String name;


  public Integer getId() {
    return id;
  }

  public void setId(Integer id) {
    this.id = id;
  }


  public Integer getSubwayId() {
    return subwayId;
  }

  public void setSubwayId(Integer subwayId) {
    this.subwayId = subwayId;
  }


  public String getName() {
    return name;
  }

  public void setName(String name) {
    this.name = name;
  }

}
