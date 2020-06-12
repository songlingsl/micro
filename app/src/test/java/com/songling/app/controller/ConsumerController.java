package com.songling.app.controller;

import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/consumer")
public class ConsumerController {

    @GetMapping("/echo-feign/{str}")
    public String feign(@PathVariable String str) {
        return "";
        //return echoService.echo(str);
    }
}
