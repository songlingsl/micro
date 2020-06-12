package com.songling.app.conf;

import com.alibaba.cloud.sentinel.annotation.SentinelRestTemplate;
import com.netflix.loadbalancer.*;
import org.springframework.cloud.client.loadbalancer.LoadBalanced;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.client.RestTemplate;
@Configuration
public class RestTemplateConf {

    @LoadBalanced
    @Bean
    @SentinelRestTemplate(urlCleanerClass = UrlCleaner.class, urlCleaner = "clean")
    public RestTemplate restTemplate() {
        return new RestTemplate();
    }


//    @LoadBalanced  不带sentinel
//    @Bean
//    public RestTemplate restTemplate() {
//        return new RestTemplate();
//    }

    /**自定义配置ribbon负载均衡算法  不起作用！！
     * @return
     */
//    @Bean
//    public IRule myRule(){
//        //return new RandomRule(); //随机
//       // return new RoundRobinRule();//轮询
//
//        //return new RetryRule();//重试
//        return  new BestAvailableRule();
//    }


}
