package org.lynn.idworker.contract;

/**
 * Copyright @ 2013QIANLONG.
 * All right reserved.
 * Class Name : org.lynn.idworker.contract
 * Description :
 * Author : cailinfeng
 * Date : 2016/8/5
 */
public interface IdGenContract {

    String genId();

    String genIdUserDefined(Integer businessId);

}
