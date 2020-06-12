package com.songling.app.feignservice;

import org.springframework.web.bind.annotation.GetMapping;


public interface EchoService {
    @GetMapping("/echo/{str}")
    String echo(String str);
}
