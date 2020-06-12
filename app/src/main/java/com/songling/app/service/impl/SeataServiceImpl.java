package com.songling.app.service.impl;

import com.songling.app.entity.Songling;
import com.songling.app.feignservice.ConsumerService;
import com.songling.app.mapper.SonglingMapper;
import com.songling.app.service.SeataService;
import io.seata.core.context.RootContext;
import io.seata.spring.annotation.GlobalTransactional;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;
@Slf4j
@Service
public class SeataServiceImpl implements SeataService {
    @Resource
    SonglingMapper songlingMapper;
    @Resource
    ConsumerService consumerService;
    @Override
    @GlobalTransactional(timeoutMills = 300000, name = "spring-cloud-demo-tx")
    public void testSeataAtModel() {
        log.info("本地保存开始");
        Songling sl=new Songling();
        sl.setName("宋");
        songlingMapper.insert(sl);
        log.info("本地保存结束");
        log.info("标记本次事务的xid: " + RootContext.getXID());

        log.info("远程保存开始");
        consumerService.seata2();
        log.info("远程保存结束");

    }
}
