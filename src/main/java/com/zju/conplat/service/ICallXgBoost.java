package com.zju.conplat.service;

import java.util.Map;

/**
 * 调用XGBoost模型
 * @author civeng
 */
public interface ICallXgBoost {

    /**
     * 根据4项参数的输入，得到预测值
     * @param paramData 模型输入
     * @return 预测值Qos
     */
    public Object predictQos(Map paramData);
}
