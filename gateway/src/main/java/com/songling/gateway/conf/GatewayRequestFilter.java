package com.songling.gateway.conf;

import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.springframework.boot.autoconfigure.security.oauth2.resource.OAuth2ResourceServerProperties;
import org.springframework.cloud.gateway.filter.GatewayFilterChain;
import org.springframework.cloud.gateway.filter.GlobalFilter;
import org.springframework.core.Ordered;
import org.springframework.core.io.buffer.DataBuffer;
import org.springframework.http.HttpStatus;
import org.springframework.http.server.reactive.ServerHttpResponse;
import org.springframework.stereotype.Component;
import org.springframework.web.server.ServerWebExchange;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

import java.nio.charset.StandardCharsets;
import java.util.Arrays;

@Slf4j
@Component
public class GatewayRequestFilter  implements GlobalFilter, Ordered {
    private String[] skipAuthUrls = {"/uaa/"};//不校验uaa的token，因为要去认证获取token
    @Override
    public Mono<Void> filter(ServerWebExchange exchange, GatewayFilterChain chain) {

        String url = exchange.getRequest().getURI().getPath();
        //跳过不需要验证的路径
        if ( Arrays.asList(skipAuthUrls).contains(url)) {
            log.info("不需要拦截的url"+url);
            return chain.filter(exchange);
        }
        log.info("需要拦截的url"+url);
        ServerHttpResponse resp = exchange.getResponse();
         //获取token
        String token = exchange.getRequest().getHeaders().getFirst("Authorization");
        if(StringUtils.isBlank(token)){
                 resp.setStatusCode(HttpStatus.UNAUTHORIZED);
                 return resp.setComplete();
        }


        return chain.filter(exchange);
    }

    @Override
    public int getOrder() {
        return 0;
    }

    /**
     * 认证错误输出
     * @param resp 响应对象
     * @param mess 错误信息
     * @return
     */
//    private Mono<Void> authErro(ServerHttpResponse resp,String mess) {
//        resp.setStatusCode(HttpStatus.UNAUTHORIZED);
//        resp.getHeaders().add("Content-Type","application/json;charset=UTF-8");
//        DataBuffer buffer = resp.bufferFactory().wrap(mess.getBytes(StandardCharsets.UTF_8));
//        return resp.writeWith(Flux.just(buffer));
//    }


}
