package com.tj.demo.xunwu.config;

import org.modelmapper.ModelMapper;
import org.springframework.context.annotation.Bean;
import org.springframework.stereotype.Controller;

@Controller
public class WebMvcConfig {


    /**
     * Bean util
     * @return
     */
    @Bean
    public ModelMapper modelMapper(){
        return new ModelMapper();
    }
}
