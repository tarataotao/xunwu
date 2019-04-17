package com.tj.demo.xunwu.repository;

import com.tj.demo.xunwu.entity.Role;
import org.springframework.data.repository.CrudRepository;

import java.util.List;

/**
 * 角色数据dao
 */
public interface RoleRepository extends CrudRepository<Role,Integer> {
    List<Role> findRolesByUserId(Integer userId);
}
