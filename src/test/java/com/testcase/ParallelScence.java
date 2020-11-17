package com.testcase;

import org.testng.annotations.Test;
import org.testng.log4testng.Logger;


/**
 * 模拟多个接口同时进行，对数据库操作场景
 */
public class ParallelScence {
    public static Logger logger = Logger.getLogger(ParallelScence.class);

    @Test(threadPoolSize = 5,invocationCount = 50)
    public void xf(){
        long threadId = Thread.currentThread().getId();
        logger.info("线索下发++，线程ID===》" + threadId);
    }

    @Test(threadPoolSize = 10,invocationCount = 50)
    public void qd(){
        long threadId = Thread.currentThread().getId();
        logger.info("商家抢单++，线程ID==》" + threadId);
    }

}
