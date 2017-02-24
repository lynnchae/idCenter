package org.lynn.idworker.openapi.hook;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.net.InetAddress;
import java.net.UnknownHostException;
import java.util.HashMap;
import java.util.Map;

/**
 * Copyright @ 2013QIANLONG.
 * All right reserved.
 * Class Name : org.lynn.idworker
 * Description :
 * Author : cailinfeng
 * Date : 2017/2/22
 */
public class ShutdownHookConfig {

    private static final Logger logger = LoggerFactory.getLogger(ShutdownHookConfig.class);

    public static final String MACHINEID = "id.machineid";

    public static final String WORKID = "id.workerid";

    public static String ip = "";

    public static Map workidmap = new HashMap<>();
    public static Map machineidmap = new HashMap<>();

    static {
        try {
            ip = InetAddress.getLocalHost().getHostAddress();
            ip = ip.replace(".","|");
        } catch (UnknownHostException e) {
            e.printStackTrace();
        }
    }

    static {
//        Runtime.getRuntime().addShutdownHook(new Thread(new Runnable() {
//            public void run() {
//                Map workidMap = new HashMap();
//                Map machineidMap = new HashMap();
//                String workerids = CacheManager.getActiveMapValue(WORKID);
//                String machineids = CacheManager.getActiveMapValue(MACHINEID);
//                if (!isBlank(workerids)) {
//                    workidMap = JSONObject.parseObject(workerids, HashMap.class);
//                }
//                if (!isBlank(machineids)) {
//                    machineidMap = JSONObject.parseObject(machineids, HashMap.class);
//                }
//                try {
//                    String valuework = URLEncoder.encode(toJSONString(workidMap),"UTF-8");
//                    String valuemachine = URLEncoder.encode(toJSONString(machineidMap),"UTF-8");
//                    CacheManager.modifyActiveValue(WORKID, valuework);
//                    CacheManager.modifyActiveValue(MACHINEID, valuemachine);
//                } catch (UnknownHostException e) {
//                } catch (IOException e) {
//                }
//                if (logger.isInfoEnabled()) {
//                    logger.info("ClearCurrentIdworker shutdown hook now.");
//                    logger.info("=====> current worker [{}]", CacheManager.getActiveMapValue(WORKID));
//                }
//            }
//        }, "ClearCurrentIdworker"));
    }
}
