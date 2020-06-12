package com.songling.app.feignservice;

import com.songling.app.vo.User;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.stereotype.Service;
import org.springframework.web.bind.annotation.*;

@Service
@FeignClient(name = "rbac")
public interface ConsumerService {
    @GetMapping("/echo/{str}")
    String echo(@PathVariable("str") String str);

    @PostMapping("/viewUser")
    public User viewUser( @RequestBody User user);

    @RequestMapping("/seata2")
    public void seata2();
}
