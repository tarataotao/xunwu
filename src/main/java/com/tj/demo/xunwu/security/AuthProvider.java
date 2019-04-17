package com.tj.demo.xunwu.security;

import com.tj.demo.xunwu.entity.User;
import com.tj.demo.xunwu.service.IUserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.authentication.AuthenticationCredentialsNotFoundException;
import org.springframework.security.authentication.AuthenticationProvider;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;

/**
 * 自定义认证实现
 */
public class AuthProvider implements AuthenticationProvider{
    @Autowired
    private IUserService userService;

    private final PasswordEncoder passwordEncoder=new BCryptPasswordEncoder();

    @Override
    public Authentication authenticate(Authentication authentication) throws AuthenticationException {
//        userService
        String userName=authentication.getName();
        String inputPassword= (String) authentication.getCredentials();
        User user=userService.findUserByName(userName);
        if(user==null){
            throw  new AuthenticationCredentialsNotFoundException("authError");
        }
        if(this.passwordEncoder.matches(inputPassword,user.getPassword())){
            return new UsernamePasswordAuthenticationToken(user,null,user.getAuthorities());
        }
//        return null;
        throw new BadCredentialsException("authError");
    }

    @Override
    public boolean supports(Class<?> aClass) {
        return true;//支持所有的认证类
    }


}
