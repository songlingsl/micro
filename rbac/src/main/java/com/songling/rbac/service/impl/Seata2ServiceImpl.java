package com.songling.rbac.service.impl;

import com.songling.rbac.entity.Plate;
import com.songling.rbac.mapper.PlateMapper;
import com.songling.rbac.service.Seata2Service;
import io.seata.core.context.RootContext;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import javax.annotation.Resource;
@Slf4j
@Service
public class Seata2ServiceImpl implements Seata2Service {
    @Resource
    PlateMapper pateMapper;
    @Override
    public void savePlate() {
        log.info("标记本次事务的xid: " + RootContext.getXID());
        log.info("保存餐盘前");
        Plate p=new Plate();
        //p.setPlateUrl("UrlUrlUrl");
        p.setPlateUrl("U");
        pateMapper.insert(p);//把url字段变小，异常后确实调用方没有插入，分布式事务生效
        log.info("保存餐盘后");
        //抛出异常
        String s="";
        s.substring(4);//抛出异常后调用方也没有插入，分布式事务生效，方法不用 @Transactional
    }
}
