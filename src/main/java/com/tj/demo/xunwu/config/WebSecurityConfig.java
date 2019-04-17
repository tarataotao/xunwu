package com.tj.demo.xunwu.config;


import com.tj.demo.xunwu.security.AuthProvider;
import com.tj.demo.xunwu.security.LoginAuthFailHandler;
import com.tj.demo.xunwu.security.LoginUrlEntryPoint;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.annotation.authentication.builders.AuthenticationManagerBuilder;
import org.springframework.security.config.annotation.method.configuration.EnableGlobalMethodSecurity;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.builders.WebSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configuration.WebSecurityConfigurerAdapter;
import org.springframework.security.core.userdetails.User;


/**
 * security 权限控制
 *///@EnableWebSecurity
//@EnableGlobalMethodSecurity
@Configuration
@EnableWebSecurity
public class WebSecurityConfig extends WebSecurityConfigurerAdapter {

    /**
     * HTTP权限控制
     * @param http
     * @throws Exception
     */
    @Override
    protected void configure(HttpSecurity http) throws Exception {
        //资源访问权限
        http.authorizeRequests()
                .antMatchers("/admin/login").permitAll()//管理员登录入口
                .antMatchers("/static/**").permitAll()//静态资源
                .antMatchers("/user/login").permitAll()//用户登录入口
                /*.antMatchers("/admin/**").hasRole("ADMIN")
                .antMatchers("/user/**").hasAnyRole("ADMIN","USER")*/
                .antMatchers("/admin/**").permitAll()
                .antMatchers("/user/**").permitAll()
                .antMatchers("/api/user/**").hasAnyRole("ADMIN","USER")
                .and()
                .formLogin()
                .loginProcessingUrl("/login") //配置角色登录处理入口
                .failureHandler(authFailHandler())
                .and()
                .logout()
                .logoutUrl("/logout")
                .logoutSuccessUrl("/logout/page")
                .deleteCookies("JSESSIONID")
                .invalidateHttpSession(true) //会话失效
                .and()
                .exceptionHandling()
                .authenticationEntryPoint(urlEntryPoint())
                .accessDeniedPage("/403") //无权限访问页面;
                ;

        http.csrf().disable();//跨域保护关闭
        http.headers().frameOptions().sameOrigin();//开启同源

    }

    @Override
    public void configure(WebSecurity web) throws Exception {
       web.ignoring().antMatchers("/static/**");
    }

    @Override
    protected void configure(AuthenticationManagerBuilder auth) throws Exception {
        auth.authenticationProvider(authProvider()).eraseCredentials(true);
    }

    @Bean
    public AuthProvider authProvider(){
        return new AuthProvider();
    }

    @Bean
    public LoginUrlEntryPoint urlEntryPoint(){
        return new LoginUrlEntryPoint("/user/login");
    }

    @Bean
    public LoginAuthFailHandler authFailHandler(){
        return new LoginAuthFailHandler(urlEntryPoint());
    }
}
