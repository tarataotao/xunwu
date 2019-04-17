package com.tj.demo.xunwu.service.impl;

import com.tj.demo.xunwu.dto.UserDTO;
import com.tj.demo.xunwu.entity.Role;
import com.tj.demo.xunwu.entity.User;
import com.tj.demo.xunwu.repository.RoleRepository;
import com.tj.demo.xunwu.repository.UserRepository;
import com.tj.demo.xunwu.service.IUserService;
import com.tj.demo.xunwu.service.ServiceResult;
import org.hibernate.annotations.NaturalId;
import org.modelmapper.ModelMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.authentication.DisabledException;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

/**
 *
 */
@Service
public class UserServiceImpl implements IUserService {

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private RoleRepository roleRepository;

    @Autowired
    private ModelMapper modelMapper;

    @Override
    public User findUserByName(String userName) {
        User user= userRepository.findByName(userName);
        if(null == user){
            return null;
        }
        List<Role> roleList=roleRepository.findRolesByUserId(user.getId());
        if(null==roleList || roleList.isEmpty() ){
            throw new DisabledException("权限非法");
        }
        List<GrantedAuthority> authorities=new ArrayList<>();
        roleList.forEach(role->authorities.add(new SimpleGrantedAuthority(role.getName())));
        user.setAuthorityList(authorities);
        return user;
    }

    @Override
    public ServiceResult<UserDTO> findById(Long userId) {
        Optional<User> userOptional=userRepository.findById(Integer.valueOf(userId+""));
        User user=userOptional.get();
        if(user==null){
            return ServiceResult.notFound();
        }

        UserDTO userDTO=modelMapper.map(user,UserDTO.class);

        return ServiceResult.of(userDTO);
    }
}
