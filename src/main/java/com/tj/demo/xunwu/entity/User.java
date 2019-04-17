package com.tj.demo.xunwu.entity;

import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;

import javax.persistence.*;
import java.util.Collection;
import java.util.Date;
import java.util.List;

@Entity
@Table(name = "user")
public class User implements UserDetails{

  @Id
  @GeneratedValue(strategy = GenerationType.IDENTITY)
  private Integer id;
  private String name;
  private String email;
  @Column(name = "phone_number")
  private String phoneNumber;
  private String password;
  private Integer status;
  @Column(name="create_time")
  private java.util.Date createTime;
  @Column(name="last_login_time")
  private java.util.Date lastLoginTime;
  @Column(name="last_update_time")
  private java.util.Date lastUpdateTime;
  private String avatar;


  @Transient
  private List<GrantedAuthority> authorityList;

  public List<GrantedAuthority> getAuthorityList() {
    return authorityList;
  }

  public void setAuthorityList(List<GrantedAuthority> authorityList) {
    this.authorityList = authorityList;
  }

  public Integer getId() {
    return id;
  }

  public void setId(Integer  id) {
    this.id = id;
  }

  public String getName() {
    return name;
  }

  public void setName(String name) {
    this.name = name;
  }

  public String getEmail() {
    return email;
  }

  public void setEmail(String email) {
    this.email = email;
  }

  public String getPhoneNumber() {
    return phoneNumber;
  }

  public void setPhoneNumber(String phoneNumber) {
    this.phoneNumber = phoneNumber;
  }

  @Override
  public Collection<? extends GrantedAuthority> getAuthorities() {
    return this.authorityList;
  }

  public String getPassword() {
    return password;
  }

  @Override
  public String getUsername() {
    return name;
  }

  @Override
  public boolean isAccountNonExpired() {
    return true;
  }

  @Override
  public boolean isAccountNonLocked() {
    return true;
  }

  @Override
  public boolean isCredentialsNonExpired() {
    return true;
  }

  @Override
  public boolean isEnabled() {
    return true;
  }

  public void setPassword(String password) {
    this.password = password;
  }

  public Integer getStatus() {
    return status;
  }

  public void setStatus(Integer status) {
    this.status = status;
  }

  public Date getCreateTime() {
    return createTime;
  }

  public void setCreateTime(Date createTime) {
    this.createTime = createTime;
  }

  public Date getLastLoginTime() {
    return lastLoginTime;
  }

  public void setLastLoginTime(Date lastLoginTime) {
    this.lastLoginTime = lastLoginTime;
  }

  public Date getLastUpdateTime() {
    return lastUpdateTime;
  }

  public void setLastUpdateTime(Date lastUpdateTime) {
    this.lastUpdateTime = lastUpdateTime;
  }

  public String getAvatar() {
    return avatar;
  }

  public void setAvatar(String avatar) {
    this.avatar = avatar;
  }
}
