package com.songling.uaa.conf;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.HttpMethod;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.oauth2.config.annotation.configurers.ClientDetailsServiceConfigurer;
import org.springframework.security.oauth2.config.annotation.web.configuration.AuthorizationServerConfigurerAdapter;
import org.springframework.security.oauth2.config.annotation.web.configuration.EnableAuthorizationServer;
import org.springframework.security.oauth2.config.annotation.web.configurers.AuthorizationServerEndpointsConfigurer;
import org.springframework.security.oauth2.config.annotation.web.configurers.AuthorizationServerSecurityConfigurer;
import org.springframework.security.oauth2.provider.ClientDetailsService;
import org.springframework.security.oauth2.provider.code.AuthorizationCodeServices;
import org.springframework.security.oauth2.provider.code.InMemoryAuthorizationCodeServices;
import org.springframework.security.oauth2.provider.token.AuthorizationServerTokenServices;
import org.springframework.security.oauth2.provider.token.DefaultTokenServices;
import org.springframework.security.oauth2.provider.token.TokenEnhancerChain;
import org.springframework.security.oauth2.provider.token.TokenStore;
import org.springframework.security.oauth2.provider.token.store.InMemoryTokenStore;
import org.springframework.security.oauth2.provider.token.store.JwtAccessTokenConverter;
import org.springframework.security.oauth2.provider.token.store.JwtTokenStore;

import java.util.Arrays;

@Configuration
//开启oauth2,auth server模式
@EnableAuthorizationServer
public class AuthorizationServerConfig extends AuthorizationServerConfigurerAdapter {

    @Autowired
    private PasswordEncoder passwordEncoder;


    @Autowired
    private ClientDetailsService clientDetailsService;
    @Autowired
    AuthorizationCodeServices authorizationCodeServices;
    //密码模式才需要配置,认证管理器，在Securityconfig中
    @Autowired
    private AuthenticationManager authenticationManager;

    /**授权码模式：
     * 操作第一步，get访问授权服务器
     *   127.0.0.1:7000/oauth/authorize?client_id=client1&response_type=code&scope=scope1&redirect_url=http://www.baidu.com
     *第二步  用户密码：都是userDetailsService配置的admin，获取授权码（url会定位到资源服务的url，服务拿到授权码）
     *
     *第三 步：post访问授权服务器，获取令牌（资源服务通过授权码去拿到token）
     *127.0.0.1:7000/oauth/token?client_id=client1&client_secret=123123&grant_type=authorization_code&code=授权码&redirect_uri=http://www.baidu.com
     *
     * 简化模式，没有后台服务的应用，会直接获取到token，很少用。
     *
     * 密码模式(自己服务)：
     * 直接访问127.0.0.1:7000/oauth/token?client_id=client1&client_secret=123123&grant_type=password&username=admin&password=admin&redirect_uri=http://www.baidu.com
     *
     * 客户端模式：自己服务内部。不需要用户名密码
     *
     * 第四步:访问资源服务器app
     * 先可以校验一下：
     * post访问127.0.0.1:7000/oauth/check_token?client_id=client1&client_secret=123123&token=获取的token
     * 然后根据oauth2的访问标准，设置header中的属性Authorization 值Bearer a828551c-2f5e-477c-9385-9083ee62876c
     * get访问127.0.0.1:9001/testoauth
     *
     * @param clients
     * @throws Exception
     */
   //配置第一项--》客户详情服务
    @Override
    public void configure(ClientDetailsServiceConfigurer clients) throws Exception {
        clients.inMemory()
                //client的id和密码
                .withClient("client1")
                .secret(passwordEncoder.encode("123123"))
                .resourceIds("resource1")//可访问的资源列表
                //允许的申请token的方式,测试用例在test项目里都有.
                //authorization_code授权码模式,这个是标准模式
                //implicit简单模式,这个主要是给无后台的纯前端项目用的
                //password密码模式,直接拿用户的账号密码授权,不安全
                //client_credentials客户端模式,用clientid和密码授权,和用户无关的授权方式
                //refresh_token使用有效的refresh_token去重新生成一个token,之前的会失效
                .authorizedGrantTypes("authorization_code", "password", "client_credentials", "implicit", "refresh_token")

                //授权的范围,每个resource会设置自己的范围.
                .scopes("scope1", "scope2")

                //这个是设置要不要弹出确认授权页面的.
                .autoApprove(false)

                //这个相当于是client的域名,重定向给code的时候会跳转这个域名
                .redirectUris("http://www.baidu.com");

                /*.and()
                .withClient("client2")
                .secret(passwordEncoder.encode("123123"))
                .resourceIds("resource1")
                .authorizedGrantTypes("authorization_code", "password", "client_credentials", "implicit", "refresh_token")
                .scopes("all")
                .autoApprove(false)
                .redirectUris("http://www.qq.com");*/
    }

    //配置第二项--》令牌访问端点,/oauth/anthorize,/oauth/token,/oauth/check_token等
    @Override
    public void configure(AuthorizationServerEndpointsConfigurer endpoints) throws Exception {
        endpoints.authenticationManager(authenticationManager)//认证管理器,在securityConfig中的bean
                .authorizationCodeServices(slAuthorizationCode())//授权码管理
                .tokenServices(tokenServices())//下面的token管理服务
                .allowedTokenEndpointRequestMethods(HttpMethod.POST);
    }

    //配置第三项--》哪些接口可以被访问
    @Override
    public void configure(AuthorizationServerSecurityConfigurer security) throws Exception {
        security.tokenKeyAccess("permitAll()")///oauth/token_key公开,主要是内网访问用，别的资源服务来访问
                .checkTokenAccess("permitAll()")///oauth/check_token公开，主要是内网访问用 /oauth/check_token，别的资源服务来访问
                .allowFormAuthenticationForClients();//允许表单认证
    }


    //配置token管理服务
    public AuthorizationServerTokenServices tokenServices() {

        DefaultTokenServices defaultTokenServices = new DefaultTokenServices();
        defaultTokenServices.setClientDetailsService(clientDetailsService);
        defaultTokenServices.setSupportRefreshToken(true);

        //token因为用了jwt所以要加这个增强
        TokenEnhancerChain tokenEnhancerChain = new TokenEnhancerChain();
        tokenEnhancerChain.setTokenEnhancers(Arrays.asList(tokenConverter()));
        defaultTokenServices.setTokenEnhancer(tokenEnhancerChain);

        //配置token的存储方法
        defaultTokenServices.setTokenStore(tokenStore());
        defaultTokenServices.setAccessTokenValiditySeconds(300);//令牌有效期 300秒
        defaultTokenServices.setRefreshTokenValiditySeconds(1500);//令牌刷新
        return defaultTokenServices;

    }
    //配置token存储方法
    public TokenStore tokenStore() {

        //第一种，return new InMemoryTokenStore();//token先存内存，普通令牌，资源服务需要访问授权服务，性能不好

        //第二种JWT
        //使用对称秘钥加密token,资源服务那边会用这个秘钥校验token,
        //还要在令牌管理服务加入这个converter
        return new JwtTokenStore(tokenConverter());
    }

    public JwtAccessTokenConverter tokenConverter() {
        JwtAccessTokenConverter converter = new JwtAccessTokenConverter();
 
        converter.setSigningKey("songling");
        return converter;
    }




    @Bean
    public AuthorizationCodeServices  slAuthorizationCode(){//授权码模式用的
        return new InMemoryAuthorizationCodeServices();

    }

}
