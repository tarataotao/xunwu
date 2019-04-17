package com.tj.demo.xunwu.controller;

import com.tj.demo.xunwu.base.ApiResponse;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ResponseBody;

@Controller
public class HomeController {

    @GetMapping(value = {"/","/index"})
    public String index(Model model){
        model.addAttribute("name","陶杰测试");
        return "index";
    }

    @GetMapping("/404")
    public String notFoundPaeg(){ return "404";}

    @GetMapping("/403")
    public String accessError(){ return "403";}

    @GetMapping("/500")
    public String internalError(){ return "500";}

    @GetMapping("/logout/page")
    public String logoutPage(){
        return "logout";
    }
}
