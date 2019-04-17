package com.tj.demo.xunwu.service;

import com.tj.demo.xunwu.dto.UserDTO;
import com.tj.demo.xunwu.entity.User;

/**
 * 用户服务
 */
public interface IUserService {

    User findUserByName(String userName);

    ServiceResult<UserDTO> findById(Long userId);
}
