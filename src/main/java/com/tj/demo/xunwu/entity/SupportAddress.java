package com.tj.demo.xunwu.entity;

import javax.persistence.*;

@Entity
@Table(name="support_address")
public class SupportAddress {

  @Id
  @GeneratedValue(strategy = GenerationType.IDENTITY)
  private Integer id;
  /**
   * 上一级行政单位
   */
  @Column(name="belong_to")
  private String belongTo;
  @Column(name="en_name")
  private String enName;
  @Column(name="cn_name")
  private String cnName;
  private String level;
  @Column(name="baidu_map_lng")
  private double baiduMapLng;
  @Column(name="baidu_map_lat")
  private double baiduMapLat;


  public Integer getId() {
    return id;
  }

  public void setId(Integer id) {
    this.id = id;
  }


  public String getBelongTo() {
    return belongTo;
  }

  public void setBelongTo(String belongTo) {
    this.belongTo = belongTo;
  }


  public String getEnName() {
    return enName;
  }

  public void setEnName(String enName) {
    this.enName = enName;
  }


  public String getCnName() {
    return cnName;
  }

  public void setCnName(String cnName) {
    this.cnName = cnName;
  }


  public String getLevel() {
    return level;
  }

  public void setLevel(String level) {
    this.level = level;
  }


  public double getBaiduMapLng() {
    return baiduMapLng;
  }

  public void setBaiduMapLng(double baiduMapLng) {
    this.baiduMapLng = baiduMapLng;
  }


  public double getBaiduMapLat() {
    return baiduMapLat;
  }

  public void setBaiduMapLat(double baiduMapLat) {
    this.baiduMapLat = baiduMapLat;
  }

  public enum Level{
      CITY("city"),
    REGION("region");

      private String value;
      Level(String value){
        this.value=value;
      }

    public String getValue() {
      return value;
    }

    public void setValue(String value) {
      this.value = value;
    }

    public static Level of(String value){
        for (Level level:Level.values()){
          if(level.getValue().equals(value)){
            return level;
          }
        }
        throw  new IllegalArgumentException();
    }
  }

}
