package com.songling.rbac.controller;

import com.songling.rbac.service.Seata2Service;
import com.songling.rbac.vo.User;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.*;

import javax.annotation.Resource;
import java.util.Map;

@RestController
@Slf4j
public class ProviderController {
    @Resource
    Seata2Service sata2Service;
    @GetMapping("/echo/{string}")
    public String echo(@PathVariable String string) {
        log.info("调用谁"+Thread.currentThread().getName());

        return "你调用提供的rbac" + string;
    }

    @PostMapping("/viewUser")
    public User updateUser( @RequestBody User user){
        System.out.println("传过来的username："+user.getUserName());
        user.setUserName("新name");
        return user;
    }

    @RequestMapping("/seata2")
    public String seata() {
        sata2Service.savePlate();
        return "seata2阶段已执行";

    }


    @RequestMapping("/hellorouterrbac")
    public String hellorouterrbac() {
        log.info("调用谁"+Thread.currentThread().getName());
        return "已通过网关路由到rbac";

    }
}
