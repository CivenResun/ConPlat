package com.zju.conplat.service;

import java.util.Map;

/**
 * @author civeng
 */
public interface CallXgboost {

    /**
     * @param paramData 模型输入
     * @return 预测值Qos
     */
    public Object predictQos(Map paramData);
}
