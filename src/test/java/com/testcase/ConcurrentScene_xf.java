package com.testcase;

import com.github.tomakehurst.wiremock.WireMockServer;
import org.testng.annotations.AfterClass;
import org.testng.annotations.BeforeClass;
import org.testng.annotations.Test;
import org.testng.log4testng.Logger;

import static com.github.tomakehurst.wiremock.client.WireMock.configureFor;
import static com.github.tomakehurst.wiremock.core.WireMockConfiguration.wireMockConfig;

/**
 * 模拟下发接口的并发场景
 * 因并发需要真实的接口逻辑，不易构造，暂用以下方式模拟
 */
public class ConcurrentScene_xf {
    public static Logger logger = Logger.getLogger(ConcurrentScene_xf.class);
    int count = 0;
    int phoneInCrease = 0;

    //同一条数据多次请求,线程池最多10条数据，并发100条数据
    @Test(threadPoolSize = 10,invocationCount = 100)
    public void test_concurrent_one(){
        String activityName = "平台+下发+S";
        Long phone = 14400000000L;
        String carName = "特斯拉";
        String cityName = "北京";
        String leadsLevel = "S";
        String price = "22.33";
        String providerId = "234";
        String source = "1";
        xfMq(activityName,phone,carName,cityName,leadsLevel,price,providerId,source);
        count ++;
    }

    //同一时间，不同数据请求,加锁避免出现脏数据情况
    @Test(threadPoolSize = 10,invocationCount = 100,timeOut = 5000)
    public void test_concurrent_more(){
        String activityName = "平台+下发+S";
        long phone = 14400000000L;
        String carName = "特斯拉";
        String cityName = "北京";
        String leadsLevel = "S";
        String price = "22.33";
        String providerId = "234";
        String source = "1";
        synchronized (this){
            phone = phone + phoneInCrease;
            long threadId = Thread.currentThread().getId();
            logger.info("线程号为==》"+threadId);
            xfMq(activityName,phone,carName,cityName,leadsLevel,price,providerId,source);
            count ++;
            phoneInCrease ++;
        }
    }



    //此方法模拟该场景接口现象
    private void xfMq(String activityName, Long phone, String carName, String cityName, String leadsLevel, String price, String providerId, String source) {
        logger.info(activityName + "==>手机号：" + phone + "||车型：" + carName + "||城市：" + cityName + "||线索级别：" + leadsLevel + "||商家ID：" + providerId + "||价格：" + price);
    }

    @AfterClass
    public void after(){
        logger.info(count);
    }

}
