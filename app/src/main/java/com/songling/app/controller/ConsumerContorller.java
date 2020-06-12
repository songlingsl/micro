package com.songling.app.controller;


import com.songling.app.feignservice.ConsumerService;
import com.songling.app.service.SeataService;
import com.songling.app.vo.User;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.client.RestTemplate;

import javax.annotation.Resource;

@RestController
public class ConsumerContorller {
    @Resource
    ConsumerService  consumerService;
    @Resource
    private RestTemplate restTemplate;
    @Resource
    SeataService seataService;
    @RequestMapping("/echo/{str}")
    public String echo(@PathVariable String str) {
        return restTemplate.getForObject("http://rbac/echo/" + str,
                String.class);//普通resttemplate实现

        //return consumerService.echo(str);

    }

    @RequestMapping("/echojson/{str}")
    public String echojson(@PathVariable String str) {
        User u=new User();
        u.setUserId("555");
        u.setUserName("旧name");
        return consumerService.viewUser(u).toString();

    }

    @RequestMapping("/seata")
    public String seata() {
        seataService.testSeataAtModel();
        return "seata已执行";

    }

    @GetMapping("/hellorouterapp")//没有 @PreAuthorize  只认证通过token即可访问
        public String hellorouterapp() {

        return "已通过网关路由到app";

    }
    @GetMapping("/testoauth")
    @PreAuthorize("hasAnyAuthority('admin')")//有admin权限的才能访问这个url

    public String testoauth() {

        return "有权限并且有了结果";

    }


}
