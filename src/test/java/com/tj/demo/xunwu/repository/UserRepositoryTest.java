package com.tj.demo.xunwu.repository;

import com.tj.demo.xunwu.entity.User;
import org.junit.Assert;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.junit4.SpringRunner;

import java.util.Optional;

import static org.junit.Assert.*;

@RunWith(SpringRunner.class)
@SpringBootTest
@ActiveProfiles("test")
public class UserRepositoryTest {

    @Autowired
    private UserRepository userRepository;

    @Test
    public void testFindOne(){
       Optional<User> userOptional= userRepository.findById(1);
       User user=userOptional.get();
        Assert.assertEquals("wali",user.getName());
    }

    @Test
    public void md5(){
        String pass = "admin";
        PasswordEncoder bcryptPasswordEncoder = new BCryptPasswordEncoder();
     /*   String hashPass = bcryptPasswordEncoder.encode(pass);
        System.out.println(hashPass);*/

        boolean f = bcryptPasswordEncoder.matches("admin","$2a$10$mf8mA6c5IIMdc/QMr81juu1N2TA/tezFHyQ6EkGsoNRf/5H/xd6mC");
        System.out.println(f);


    }
}