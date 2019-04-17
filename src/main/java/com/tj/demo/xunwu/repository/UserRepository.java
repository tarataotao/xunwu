package com.tj.demo.xunwu.repository;

import com.tj.demo.xunwu.entity.User;
import org.springframework.data.repository.CrudRepository;

public interface UserRepository extends CrudRepository<User,Integer> {

    User findByName(String name);
}
