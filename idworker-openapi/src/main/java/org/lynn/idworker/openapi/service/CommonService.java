package org.lynn.idworker.openapi.service;

import com.alibaba.fastjson.JSONObject;
import org.lynn.idworker.openapi.hook.ShutdownHookConfig;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.*;

import static com.alibaba.dubbo.common.utils.StringUtils.isBlank;

/**
 * Copyright @ 2013QIANLONG.
 * All right reserved.
 * Class Name : org.lynn.idworker.utils
 * Description :
 * Author : cailinfeng
 * Date : 2017/2/22
 */
public class CommonService {

    private static final Logger logger = LoggerFactory.getLogger(CommonService.class);

    private static String workerids = "";

    private static String machineids = "";

    public CommonService() {
        workerids = "{}";
        machineids = "{}";
    }

    public Long generateCurrentid(String workOrmachine, Integer byt) {
        Map<String, Integer> idMap = new HashMap<>();
        String ids = "";
        if (workOrmachine.equals(ShutdownHookConfig.WORKID)) {
            ids = workerids;
        } else {
            ids = machineids;
        }
        if (!isBlank(ids)) {
            idMap = JSONObject.parseObject(ids, HashMap.class);
        }
        if (idMap.get(ShutdownHookConfig.ip) != null) {
            return Long.valueOf(idMap.get(ShutdownHookConfig.ip));
        }
        Random rand = new Random();
        Integer currentWorkid = rand.nextInt(byt) + 1;
        if (idMap != null && idMap.size() > 0) {
            while (checkIfEqual(currentWorkid, idMap)) {
                currentWorkid = rand.nextInt(byt) + 1;
            }
        }
        idMap.put(ShutdownHookConfig.ip, currentWorkid);
        if (workOrmachine.equals(ShutdownHookConfig.WORKID)) {
            ShutdownHookConfig.workidmap = idMap;
        } else {
            ShutdownHookConfig.machineidmap = idMap;
        }
        return Long.valueOf(currentWorkid);
    }

    public static boolean checkIfEqual(Integer randomValue, Map<String, Integer> sourceMap) {
        if (sourceMap == null || sourceMap.size() == 0) {
            return false;
        }
        boolean flag = false;
        Set<Map.Entry<String, Integer>> allSet = sourceMap.entrySet();
        Iterator<Map.Entry<String, Integer>> iter = allSet.iterator();
        while (iter.hasNext()) {
            Map.Entry<String, Integer> me = iter.next();
            if (me.getValue() == randomValue) {
                flag = true;
                break;
            }
        }
        return flag;
    }

}
