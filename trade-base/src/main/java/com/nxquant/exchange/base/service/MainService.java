package com.nxquant.exchange.base.service;

import com.nxquant.exchange.base.test.TestUtil;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

@Component
public class MainService {
    private Logger logger = LoggerFactory.getLogger(this.getClass());

    @Autowired
    TestUtil testUtil;


    public void start(){
        logger.info("{} Begin", Thread.currentThread().getName());
        test();
    }

    private void test(){
        //testUtil.testAsync();
        //int partitionId = testUtil.testGetPartitionIdByClientId("9752877235");
        //testUtil.testMeterRegistry();
        testUtil.testUserRegister();
    }
}
