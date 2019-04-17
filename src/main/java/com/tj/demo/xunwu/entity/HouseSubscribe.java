package com.tj.demo.xunwu.entity;


import javax.persistence.*;

@Entity
@Table(name="house_subscribe")
public class HouseSubscribe {

  @Id
  @GeneratedValue(strategy = GenerationType.IDENTITY)
  private Integer id;
  @Column(name="house_id")
  private Integer houseId;
  @Column(name="user_id")
  private Integer userId;
  private String desc;
  private Integer status;
  @Column(name="create_time")
  private java.util.Date createTime;
  @Column(name="last_update_time")
  private java.util.Date lastUpdateTime;
  @Column(name="order_time")
  private java.util.Date orderTime;
  private String telephone;
  @Column(name="admin_id")
  private Integer adminId;


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


  public Integer getUserId() {
    return userId;
  }

  public void setUserId(Integer userId) {
    this.userId = userId;
  }


  public String getDesc() {
    return desc;
  }

  public void setDesc(String desc) {
    this.desc = desc;
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


  public java.util.Date getOrderTime() {
    return orderTime;
  }

  public void setOrderTime(java.util.Date orderTime) {
    this.orderTime = orderTime;
  }


  public String getTelephone() {
    return telephone;
  }

  public void setTelephone(String telephone) {
    this.telephone = telephone;
  }


  public Integer getAdminId() {
    return adminId;
  }

  public void setAdminId(Integer adminId) {
    this.adminId = adminId;
  }

}
