package com.tj.demo.xunwu.security;

import org.springframework.security.core.AuthenticationException;
import org.springframework.security.web.authentication.LoginUrlAuthenticationEntryPoint;
import org.springframework.util.AntPathMatcher;
import org.springframework.util.PathMatcher;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.util.HashMap;
import java.util.Map;

/**
 * 基于角色的登录入口控制器
 */
public class LoginUrlEntryPoint  extends LoginUrlAuthenticationEntryPoint{

    private PathMatcher pathMatcher=new AntPathMatcher();
    private final Map<String,String> authEntryPontMap;

    public LoginUrlEntryPoint(String loginFormUrl) {
        super(loginFormUrl);
        authEntryPontMap=new HashMap<>();

        //普通用户登录入口映射
        authEntryPontMap.put("/user/**","/user/login");
        //管理员登录入口 映射
        authEntryPontMap.put("/admin/**","/admin/login");
    }


    /**
     * g根据请求跳转到制定的页面，父类是默认使用  loginFormUrl,就是上面的方法传来的
     * @param request
     * @param response
     * @param exception
     * @return
     */
    @Override
    protected String determineUrlToUseForThisRequest(HttpServletRequest request, HttpServletResponse response, AuthenticationException exception) {
        String uri=request.getRequestURI().replace(request.getContextPath(),"");
        for (Map.Entry<String, String> authEntry : this.authEntryPontMap.entrySet()) {
            if(this.pathMatcher.match(authEntry.getKey(),uri)){
                return authEntry.getValue();
            }
        }

        return super.determineUrlToUseForThisRequest(request, response, exception);
    }
}
