package com.songling.app.conf;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.oauth2.config.annotation.web.configuration.EnableResourceServer;
import org.springframework.security.oauth2.config.annotation.web.configuration.ResourceServerConfigurerAdapter;
import org.springframework.security.oauth2.config.annotation.web.configurers.ResourceServerSecurityConfigurer;
import org.springframework.security.oauth2.provider.token.TokenStore;
import org.springframework.security.oauth2.provider.token.store.JwtAccessTokenConverter;
import org.springframework.security.oauth2.provider.token.store.JwtTokenStore;

@Configuration

//标记成oauth的资源服务
@EnableResourceServer
public class ResourceServerConfig extends ResourceServerConfigurerAdapter {

    @Autowired
    private TokenStore tokenStore;
//    @Override//本方法用于远程校验，jwt不用
//    public void configure(ResourceServerSecurityConfigurer resources) throws Exception {
//        //远程token验证, 普通token必须远程校验
//        RemoteTokenServices tokenServices = new RemoteTokenServices();
//        //配置去哪里验证token
//        tokenServices.setCheckTokenEndpointUrl("http://127.0.0.1:7000/oauth/check_token");
//        //配置组件的clientid和密码,这个也是在auth中配置好的
//        tokenServices.setClientId("client1");
//        tokenServices.setClientSecret("123123");
//        resources.resourceId("resource1").tokenServices(tokenServices//验证令牌服务
//        ).stateless(true);
//    }

    //jwt所用校验方法
    @Override
    public void configure(ResourceServerSecurityConfigurer resources) throws Exception {
//tokenStore一定要注入，因为有默认的对象需要tokenStore
//        JwtAccessTokenConverter converter = new JwtAccessTokenConverter();
////        converter.setSigningKey("songling");//对称加密方式，与授权服务相同
////        TokenStore tokenStore = new JwtTokenStore(converter);
        resources.resourceId("resource1")
                .tokenStore(tokenStore)  //tokenStore一定要注入，因为有默认的对象需要tokenStore
                .stateless(true);//不要把token信息记录在session中
    }

    @Override
    public void configure(HttpSecurity http) throws Exception {
        http.csrf().disable()
                .authorizeRequests()
                //本项目所需要的授权范围,这个scope是写在auth服务的配置里的
                .antMatchers("/**").access("#oauth2.hasScope('scope1')")
                .and()
                .sessionManagement().sessionCreationPolicy(SessionCreationPolicy.STATELESS);//不用session了
    }

    //配置如何把普通token转换成jwt token
    @Bean
    public JwtAccessTokenConverter tokenConverter() {
        JwtAccessTokenConverter converter = new JwtAccessTokenConverter();

        //使用对称秘钥加密token,resource那边会用这个秘钥校验token
        converter.setSigningKey("songling");
        return converter;
    }

    //配置token的存储方法
    @Bean
    public TokenStore tokenStore() {
        //把用户信息都存储在token当中,相当于存储在客户端,性能好很多
        return new JwtTokenStore(tokenConverter());
    }
}
