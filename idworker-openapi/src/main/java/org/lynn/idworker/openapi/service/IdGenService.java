package org.lynn.idworker.openapi.service;

import org.lynn.common.utils.DateUtils;
import org.lynn.idworker.contract.IdGenContract;
import org.lynn.idworker.openapi.hook.ShutdownHookConfig;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.concurrent.ConcurrentLinkedDeque;

/**
 * Copyright @ 2013QIANLONG.
 * All right reserved.
 * Class Name : org.lynn.idworker.business
 * Description :
 * Author : cailinfeng
 * Date : 2016/8/5
 */
@Service
public class IdGenService implements IdGenContract {

    private static final Logger log = LoggerFactory.getLogger(IdGenService.class);

    private static ConcurrentLinkedDeque<Long> concurrentLinkedDeque = new ConcurrentLinkedDeque();

    //队列阀值
    private static int QUEUE_THREASHOLD = 2000;

    //队列最大值
    private static int QUEUE_MAX = 22000 ;

    private static long lastTimestamp = -1L;

    @Resource
    private CustomUUIDService customUUIDService;

    @Resource
    private CommonService commonService;


    public IdGenService(CustomUUIDService customUUIDService) {
        this.QUEUE_MAX = 50000;
        this.QUEUE_THREASHOLD = 500;
        this.customUUIDService = customUUIDService;
        for(int i = 0 ; i < QUEUE_MAX ; i++) {
            concurrentLinkedDeque.add(customUUIDService.generate());
        }
        new IdProducer().start();
    }

    @Override
    public String genId() {
        return String.valueOf(concurrentLinkedDeque.poll());
    }

    @Override
    public String genIdUserDefined(Integer businessId) {
        return produceId(businessId);
    }

    private synchronized String produceId(Integer businessId){
        if(businessId > 99){
            throw new RuntimeException("idGen only suppurt 99 products, please input [business] less than 100!");
        }
        StringBuffer returnId = new StringBuffer("");
        //产品前缀
        String bizId = String.valueOf(businessId);
        //日期前缀
        String datePrefix = DateUtils.convert(new Date(),"yyyyMMddHH");
        if(businessId < 10){
            bizId = "0"+businessId;
        }
        returnId.append(datePrefix).append(bizId);

        returnId.append(commonService.generateCurrentid(ShutdownHookConfig.MACHINEID,99));
        //
        String currentMillis = String.valueOf(getNextTimeStamp()).substring(3);
        returnId.append(currentMillis);
        return returnId.toString();
    }

    private long tailNextMillis(final long lastTimestamp) {
        long timestamp = this.timeGen();
        while (timestamp <= lastTimestamp) {
            timestamp = this.timeGen();
        }
        return timestamp;
    }

    // 获取当前的时间戳
    protected long timeGen() {
        return System.currentTimeMillis();
    }

    public long getNextTimeStamp(){
        long timestamp = timeGen();

        if (timestamp < lastTimestamp) {
            try {
                throw new Exception("Clock moved backwards.  Refusing to generate id for "
                        + (lastTimestamp - timestamp) + " milliseconds");
            } catch (Exception e) {
                e.printStackTrace();
            }
        }

        if (timestamp == lastTimestamp){
            timestamp = tailNextMillis(lastTimestamp);
        }
        lastTimestamp = timestamp;
        return timestamp;
    }

    class IdProducer extends Thread{

        @Override
        public void run() {
            log.info(String.format("========>  id生成器启动，初始化大小为：[%d]",QUEUE_MAX));
            List<Long> tempList = new ArrayList<>();
            while(true){
                while(concurrentLinkedDeque.size() < QUEUE_THREASHOLD){
                    tempList.clear();
                    for(int i = 0 ; i < (QUEUE_MAX - QUEUE_THREASHOLD) ; i++){
                        tempList.add(customUUIDService.generate());
                    }
                    concurrentLinkedDeque.addAll(tempList);
                    log.info(String.format("向队列取中插入[%d]元素，队列大小为:[%d]",(QUEUE_MAX - QUEUE_THREASHOLD),concurrentLinkedDeque.size()));
                }
            }
        }
    }

}

